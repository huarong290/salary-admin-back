package com.salary.admin.service.impl.salary;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.googlecode.aviator.AviatorEvaluator;
import com.salary.admin.engine.SalaryRuleEngine;
import com.salary.admin.exception.BusinessException;
import com.salary.admin.model.dto.engine.SalaryCalcBatchReqDTO;
import com.salary.admin.model.dto.engine.SalaryCalcSingleReqDTO;
import com.salary.admin.model.dto.salary.snapshot.SalaryDetailItemDTO;
import com.salary.admin.model.dto.salary.snapshot.SalarySnapshotDTO;
import com.salary.admin.model.entity.salary.SalaryCalcPipelineStep;
import com.salary.admin.model.entity.salary.SalaryItemDetail;
import com.salary.admin.model.entity.salary.SalarySummary;
import com.salary.admin.model.vo.calcrule.CalcRuleVO;
import com.salary.admin.service.salary.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 薪资核心引擎实现类
 * <p>
 * 核心定位：
 * - 作为薪资模块的“总调度室”，负责跨表、跨业务的复杂逻辑编排。
 * 架构特性：图纸化编排、Context变量透传、动态Aviator阻断、强类型DTO入参
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SalaryCoreEngineImpl implements ISalaryCoreEngine {
    // 基础业务 Service
    private final ISalaryPeriodService iSalaryPeriodService;
    private final ISalarySummaryService iSalarySummaryService;
    private final ISalaryArchiveService iSalaryArchiveService;
    private final ISalaryArchiveItemService iSalaryArchiveItemService;
    private final ISalaryItemConfigService iSalaryItemConfigService;
    //  全新计算引擎核心 Service
    private final SalaryRuleEngine salaryRuleEngine;
    private final ISalaryCalcRuleService iSalaryCalcRuleService;
    private final ISalaryCalcContextService iSalaryCalcContextService;
    private final ISalaryCalcLogService iSalaryCalcLogService;
    private final ISalaryItemDetailService iSalaryItemDetailService;
    private final ISalaryCalcPipelineStepService iSalaryCalcPipelineStepService;
    /**
     * 执行单人当月薪资核算 (瀑布流管道计算核心)
     *
     * @param reqDTO 单人核算参数指令
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void calculateEmployeeSalary(SalaryCalcSingleReqDTO reqDTO) {
        Long periodId = reqDTO.getPeriodId();
        Long employeeId = reqDTO.getEmployeeId();
        String pipelineCode = reqDTO.getPipelineCode();
        Integer pipelineVersion = reqDTO.getPipelineVersion();

        log.info("▶️ [引擎启动] 周期: [{}], 员工: [{}], 管道: [{}-V{}]", periodId, employeeId, pipelineCode, pipelineVersion);

        // 1. 获取图纸 (按 stage 和 sortOrder 排序)
        List<SalaryCalcPipelineStep> steps = iSalaryCalcPipelineStepService.lambdaQuery()
                .eq(SalaryCalcPipelineStep::getPipelineCode, pipelineCode)
                .eq(SalaryCalcPipelineStep::getPipelineVersion, pipelineVersion)
                .eq(SalaryCalcPipelineStep::getStatus, 1)
                .orderByAsc(SalaryCalcPipelineStep::getStage)
                .orderByAsc(SalaryCalcPipelineStep::getSortOrder)
                .list();

        if (steps.isEmpty()) throw new BusinessException("该薪资管道未配置核算步骤！");

        // 2. 准备原材料 (Context Env)
        Map<String, Object> env = iSalaryCalcContextService.buildEmployeeContext(periodId, employeeId);

        //  初始化你的数据载体
        List<SalaryItemDetail> detailList = new ArrayList<>();
        SalarySnapshotDTO snapshot = new SalarySnapshotDTO();
        // 初始化累加器
        BigDecimal incomeTotal = BigDecimal.ZERO;
        BigDecimal deductionTotal = BigDecimal.ZERO;
        BigDecimal taxTotal = BigDecimal.ZERO;
        BigDecimal companyExpenseTotal = BigDecimal.ZERO;

        // 3. 驱动流水线
        for (SalaryCalcPipelineStep step : steps) {
            String ruleCode = step.getRuleCode();

            try {
                // 3.1 前置拦截 (Condition Script)
                if (StringUtils.isNotBlank(step.getConditionScript())) {
                    Boolean shouldRun = (Boolean) AviatorEvaluator.execute(step.getConditionScript(), env);
                    if (!shouldRun) {
                        env.put(ruleCode, BigDecimal.ZERO);
                        continue;
                    }
                }

                // 3.2 查规则脚本
                CalcRuleVO rule = iSalaryCalcRuleService.getByRuleCode(ruleCode);

                // 3.3 Aviator 核心计算
                BigDecimal stepResult = salaryRuleEngine.execute(rule.getRuleScript(), env);

                // 3.4 空值跳过
                if (step.getSkipIfNull() == 1 && stepResult.compareTo(BigDecimal.ZERO) == 0) {
                    env.put(ruleCode, BigDecimal.ZERO);
                    continue;
                }

                env.put(ruleCode, stepResult);

                // ==========================================================
                //  3.5 智能分类并组装你定义的 Detail 实体和 DTO
                // ==========================================================
                // 假设我们通过阶段(Stage)或者你规则表里的字段来判断 itemType (1-收入, 2-扣款, 3-税费, 4-公司支出)
                // 这里用一个简单的映射示例，实际可根据你的 rule_type 或 stage 来决定
                int itemType = determineItemTypeByStage(step.getStage());

                // 组装 DB 明细实体
                SalaryItemDetail detail = new SalaryItemDetail()
                        .setPeriodId(periodId)
                        .setEmployeeId(employeeId)
                        .setItemCode(ruleCode)
                        .setItemName(step.getRuleName())
                        .setSettlementAmount(stepResult) // 结算金额
                        .setSettlementCurrency("CNY")
                        .setItemType(itemType)
                        .setSourceType(2) // 2-引擎计算
                        .setCalcPriority(step.getSortOrder());
                detailList.add(detail);

                // 组装 JSON 快照的单条 DTO
                SalaryDetailItemDTO snapshotItem = SalaryDetailItemDTO.builder()
                        .itemCode(ruleCode)
                        .itemName(step.getRuleName())
                        .settlementAmount(stepResult)
                        .source("SYSTEM_CALC")
                        .sort(step.getSortOrder())
                        .build();

                // 按类型放入快照，并进行动态累加
                switch (itemType) {
                    case 1:
                        snapshot.getIncome().add(snapshotItem);
                        incomeTotal = incomeTotal.add(stepResult);
                        break;
                    case 2:
                        snapshot.getDeduction().add(snapshotItem);
                        deductionTotal = deductionTotal.add(stepResult);
                        break;
                    case 3:
                        snapshot.getTax().add(snapshotItem);
                        taxTotal = taxTotal.add(stepResult);
                        break;
                    case 4:
                        snapshot.getCompanyExpense().add(snapshotItem);
                        companyExpenseTotal = companyExpenseTotal.add(stepResult);
                        break;
                }

            } catch (Exception e) {
                log.error("节点: {} 计算失败！原因: {}", ruleCode, e.getMessage());
                if (step.getBlockFlag() == 1) {
                    throw new BusinessException("核算中断: " + ruleCode + " 报错 - " + e.getMessage());
                } else {
                    env.put(ruleCode, BigDecimal.ZERO);
                }
            }
        }

        // =========================================================================
        // 4. 成品包装：保存明细与主表汇总
        // =========================================================================

        // 4.1 删除历史明细
        iSalaryItemDetailService.remove(new LambdaQueryWrapper<SalaryItemDetail>()
                .eq(SalaryItemDetail::getPeriodId, periodId)
                .eq(SalaryItemDetail::getEmployeeId, employeeId));

        // 4.2 批量保存新明细
        if (!detailList.isEmpty()) {
            iSalaryItemDetailService.saveBatch(detailList);
        }

        // 4.3 汇总落盘
        SalarySummary summary = iSalarySummaryService.getSummaryByUnique(periodId, employeeId);
        if (summary != null) {
            // 计算 Gross (应发) 和 Net (实发)
            // 逻辑: 实发 = 收入合计 - 扣款合计 - 税费合计
            BigDecimal grossSalary = incomeTotal;
            BigDecimal netSalary = incomeTotal.subtract(deductionTotal).subtract(taxTotal);

            // 补充全局 Snapshot 的汇总数据
            snapshot.setGrossSalary(grossSalary);
            snapshot.setDeductionTotal(deductionTotal);
            snapshot.setTaxTotal(taxTotal);
            snapshot.setNetSalary(netSalary);
            snapshot.setSettlementCurrency("CNY");

            // 将 DTO 序列化为 JSON 字符串
            String detailJsonString = JSONUtil.toJsonStr(snapshot);

            // 更新 Summary 主表
            summary.setIncomeTotal(incomeTotal)
                    .setDeductionTotal(deductionTotal)
                    .setTaxTotal(taxTotal)
                    .setGrossSalary(grossSalary)
                    .setNetSalary(netSalary)
                    .setCalcStatus(1) // 1-成功
                    .setDetailJson(detailJsonString); // 🌟 核心：注入工资条快照

            iSalarySummaryService.updateById(summary);
        }

        log.info("⏹️ 员工 [{}] 薪资核算完毕。实发金额: {}", employeeId, snapshot.getNetSalary());
    }
    /**
     * 批量执行薪资核算 (发薪台触发)
     *
     * @param reqDTO 批量核算参数指令
     */
    @Override
    public void calculateBatchSalary(SalaryCalcBatchReqDTO reqDTO) {
        log.info("🚀 启动批量薪资核算任务，涉及总人数：{}", reqDTO.getEmployeeIds().size());

        int successCount = 0;
        int failCount = 0;

        for (Long empId : reqDTO.getEmployeeIds()) {
            try {
                // 巧妙使用链式编程，将批量参数拆解为单人核算指令
                SalaryCalcSingleReqDTO singleReq = new SalaryCalcSingleReqDTO()
                        .setPeriodId(reqDTO.getPeriodId())
                        .setEmployeeId(empId)
                        .setPipelineCode(reqDTO.getPipelineCode())
                        .setPipelineVersion(reqDTO.getPipelineVersion());

                // 触发单人核算
                // 注意：生产级高并发场景下，这里可以结合 Spring 的 @Async 结合 ThreadPoolExecutor 进行多线程并发跑批
                this.calculateEmployeeSalary(singleReq);

                successCount++;
            } catch (Exception e) {
                failCount++;
                log.error("批量计算中断：员工 [{}] 计算失败，已跳过。报错信息: {}", empId, e.getMessage());
                // 批处理容错策略：单人报错仅记录日志并跳过，不阻断其他人的算薪进程
            }
        }

        log.info("🏁 批量计算跑批任务结束。✅成功: {} 人, ❌失败: {} 人", successCount, failCount);
    }

    /**
     * 辅助方法：根据核算阶段映射项目收支类型
     * (你可以根据实际业务字典来修改这个映射规则)
     */
    private int determineItemTypeByStage(Integer stage) {
        if (stage == null) return 1;
        // 假设： 1-基础, 2-津贴 (属于收入 item_type=1)
        //       3-扣款 (属于扣款 item_type=2)
        //       4-税务 (属于税费 item_type=3)
        switch (stage) {
            case 1:
            case 2: return 1; // 收入
            case 3: return 2; // 扣款
            case 4: return 3; // 税费
            default: return 1;
        }
    }
}
