package com.salary.admin.service.impl.salary.engine;

import cn.hutool.json.JSONUtil;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.salary.admin.engine.SalaryRuleEngine;
import com.salary.admin.event.CalcLogEvent;
import com.salary.admin.exception.BusinessException;
import com.salary.admin.model.dto.salary.StepExecResult;
import com.salary.admin.model.entity.salary.SalaryCalcLog;
import com.salary.admin.model.entity.salary.SalaryCalcPipelineStep;
import com.salary.admin.model.vo.calcrule.CalcRuleVO;
import com.salary.admin.service.salary.ISalaryCalcRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 流水线节点执行器
 * 职责：解析脚本、异常容错、推送异步审计埋点
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PipelineStepExecutor {

    private final SalaryRuleEngine salaryRuleEngine;
    private final ISalaryCalcRuleService iSalaryCalcRuleService;
    private final ApplicationEventPublisher eventPublisher;
    private final SalaryTaxCalculator salaryTaxCalculator;

    /** 内置 Java 计税节点的规则编码 (累计预扣预缴法) */
    private static final String AUTO_TAX_RULE_CODE = "AUTO_TAX_CALC";

    public StepExecResult executeStep(SalaryCalcPipelineStep step, Map<String, Object> env, Long periodId, Long employeeId) {
        long startTime = System.currentTimeMillis();
        BigDecimal stepResult = BigDecimal.ZERO;
        String errorMsg = null;
        String ruleCode = step.getRuleCode();
        //  默认结果设为跳过，确保在任何异常中断下都有保底的返回状态
        StepExecResult finalResult = StepExecResult.skip();
        try {
            // 1. 前置动态阻断 (Condition Script)
            if (StringUtils.isNotBlank(step.getConditionScript())) {
                // 使用 compile 并开启缓存 (true)。内部会将 script 的 md5 作为 key 缓存编译好的 Expression
                Expression expression = AviatorEvaluator.getInstance().compile(step.getConditionScript(), step.getConditionScript(), true);
                Boolean shouldRun = (Boolean) expression.execute(env);
                if (!shouldRun){
                    // 条件不满足 -> 明确返回 skip()
                    return finalResult;
                }
            }

            // 2. 执行规则: 内置 Java 节点走计税组件, 其余节点走 Aviator 脚本
            if (AUTO_TAX_RULE_CODE.equals(ruleCode)) {
                // ① 档案级个税规则门禁: salary_archive.tax_rule_code = NO_TAX(或未配置) 时, 该员工本期不计税
                String taxRuleCode = env.get("taxRuleCode") != null
                        ? String.valueOf(env.get("taxRuleCode")).trim() : null;
                if (StringUtils.isBlank(taxRuleCode) || "NO_TAX".equalsIgnoreCase(taxRuleCode)) {
                    log.info("员工[{}] 档案个税规则为[{}], 本期不计税", employeeId, taxRuleCode);
                    stepResult = BigDecimal.ZERO;
                } else {
                    // ② 个税节点: 使用累计预扣预缴法, 计税基数 = 应税收入 (排除 taxable_flag=0 的不计税项)
                    String settlementMonth = env.get("settlementMonth") != null
                            ? String.valueOf(env.get("settlementMonth")) : null;
                    BigDecimal taxableIncome = env.get("_taxableIncome") instanceof BigDecimal
                            ? (BigDecimal) env.get("_taxableIncome") : BigDecimal.ZERO;
                    stepResult = salaryTaxCalculator.calculate(employeeId, settlementMonth, taxableIncome);
                }
            } else {
                // 获取并执行规则脚本
                CalcRuleVO rule = iSalaryCalcRuleService.getByRuleCode(ruleCode);
                if (rule == null) {
                    throw new BusinessException("规则 [" + ruleCode + "] 不存在或已停用，请检查规则库配置");
                }
                stepResult = salaryRuleEngine.execute(rule.getRuleScript(), env);
            }

            // 3. 空值跳过
            if (step.getSkipIfNull() == 1 && stepResult.compareTo(BigDecimal.ZERO) == 0) {
                // 配置了空值跳过，返回明确的 skip() 语义
                return finalResult;
            }
            // 正常计算 -> 更新最终结果并返回 success() (确保审计日志记录真实金额)
            finalResult = StepExecResult.success(stepResult);
            return finalResult;

        } catch (Exception e) {
            log.error("节点: {} 计算失败！原因: {}", ruleCode, e.getMessage());
            errorMsg = e.getMessage();

            // 阻断策略：如果节点要求强阻断，则抛出异常熔断整个核算流
            if (step.getBlockFlag() == 1) {
                throw new BusinessException("核算中断: [" + ruleCode + "] 计算异常 - " + errorMsg);
            }
            // 容错降级：返回跳过状态
            return finalResult;

        } finally {
            // 清理环境变量，防止序列化风暴 (例如庞大的溯源档案集合)
            Map<String, Object> logEnv = new HashMap<>(env);
            logEnv.remove("_usedArchives");

            // 组装并推送异步审计事件
            long executeTime = System.currentTimeMillis() - startTime;
            SalaryCalcLog auditLog = new SalaryCalcLog();
            auditLog.setEmployeeId(employeeId);
            auditLog.setPeriodId(periodId);
            auditLog.setRuleCode(ruleCode);
            auditLog.setStage(step.getStage() != null ? step.getStage() : 1);
            auditLog.setInputJson(JSONUtil.toJsonStr(logEnv)); // 干净的 JSON
            // 安全提取最终决定出的金额（即使是 skip，里面也会安全的包裹着 ZERO）
            auditLog.setOutputValue(finalResult.getAmount());
            auditLog.setErrorMsg(errorMsg);
            auditLog.setExecuteTime(executeTime);

            eventPublisher.publishEvent(new CalcLogEvent(auditLog));
        }
    }
}