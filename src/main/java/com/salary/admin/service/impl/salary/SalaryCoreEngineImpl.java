package com.salary.admin.service.impl.salary;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.googlecode.aviator.AviatorEvaluator;
import com.salary.admin.engine.SalaryRuleEngine;
import com.salary.admin.event.CalcLogEvent;
import com.salary.admin.exception.BusinessException;
import com.salary.admin.model.dto.engine.SalaryCalcBatchReqDTO;
import com.salary.admin.model.dto.engine.SalaryCalcSingleReqDTO;
import com.salary.admin.model.dto.salary.period.PeriodBatchInitReqDTO;
import com.salary.admin.model.dto.salary.snapshot.ArchiveSnapshot;
import com.salary.admin.model.dto.salary.snapshot.SalaryDetailItemDTO;
import com.salary.admin.model.dto.salary.snapshot.SalarySnapshotDTO;
import com.salary.admin.model.dto.salary.summary.SummaryInitReqDTO;
import com.salary.admin.model.entity.salary.*;
import com.salary.admin.model.vo.calcrule.CalcRuleVO;
import com.salary.admin.model.vo.salary.summary.SalarySummaryVO;
import com.salary.admin.service.salary.*;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 薪资核心引擎实现类
 * <p>
 * 核心定位：
 * - 作为薪资模块的“总调度室”，负责跨表、跨业务的复杂逻辑编排。
 * 架构特性：图纸化编排、Context变量透传、动态Aviator阻断、强类型DTO入参
 * * @author system
 * @since 2026-03-31
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SalaryCoreEngineImpl implements ISalaryCoreEngine {

    // 基础业务 Service
    private final ISalaryEmployeeService iSalaryEmployeeService;
    private final ISalaryPeriodService iSalaryPeriodService;
    private final ISalarySummaryService iSalarySummaryService;
    private final ISalaryArchiveService iSalaryArchiveService;
    private final ISalaryArchiveItemService iSalaryArchiveItemService;
    private final ISalaryItemConfigService iSalaryItemConfigService;

    // 全新计算引擎核心 Service
    private final SalaryRuleEngine salaryRuleEngine;
    private final ISalaryCalcRuleService iSalaryCalcRuleService;
    private final ISalaryCalcContextService iSalaryCalcContextService;
    private final ISalaryCalcLogService iSalaryCalcLogService;
    private final ISalaryItemDetailService iSalaryItemDetailService;
    private final ISalaryCalcPipelineStepService iSalaryCalcPipelineStepService;
    private final ApplicationEventPublisher eventPublisher;

    // 🌟 【架构师优化】：注入自身代理对象，解决 this 调用导致 @Transactional 本地事务失效的问题
    @Lazy
    @Resource
    private ISalaryCoreEngine selfProxy;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean initSummaryAccount(SummaryInitReqDTO reqDTO) {
        String month = reqDTO.getSettlementMonth();
        List<Long> targetEmpIds = reqDTO.getEmployeeIds();

        // 1. 初始化底层周期 (调用已有逻辑)
        PeriodBatchInitReqDTO periodReq = new PeriodBatchInitReqDTO();
        periodReq.setSettlementMonth(month);
        periodReq.setEmployeeIds(targetEmpIds);
        iSalaryPeriodService.batchInitPeriodsOnly(periodReq);

        // 2. 以 Period 为基准获取本月数据
        LambdaQueryWrapper<SalaryPeriod> periodWrapper = new LambdaQueryWrapper<SalaryPeriod>()
                .eq(SalaryPeriod::getSettlementMonth, month);
        if (CollUtil.isNotEmpty(targetEmpIds)) {
            periodWrapper.in(SalaryPeriod::getEmployeeId, targetEmpIds);
        }
        List<SalaryPeriod> allPeriods = iSalaryPeriodService.list(periodWrapper);
        if (CollUtil.isEmpty(allPeriods)) return false;

        // 3. Diff 查出还没有 Summary 的周期
        List<Long> periodIds = allPeriods.stream().map(SalaryPeriod::getId).collect(Collectors.toList());
        Set<Long> existSummaryPeriodIds = iSalarySummaryService.list(new LambdaQueryWrapper<SalarySummary>()
                        .in(SalarySummary::getPeriodId, periodIds))
                .stream().map(SalarySummary::getPeriodId).collect(Collectors.toSet());

        List<SalaryPeriod> needInitPeriods = allPeriods.stream()
                .filter(p -> !existSummaryPeriodIds.contains(p.getId()))
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(needInitPeriods)) return true; // 都存在则直接返回成功

        // 4. 获取员工快照
        Set<Long> empIds = needInitPeriods.stream().map(SalaryPeriod::getEmployeeId).collect(Collectors.toSet());
        Map<Long, SalaryEmployee> empMap = iSalaryEmployeeService.listByIds(empIds).stream()
                .collect(Collectors.toMap(SalaryEmployee::getId, e -> e));

        // 5. 构建并落库 Summary 空壳数据
        List<SalarySummary> newSummaries = needInitPeriods.stream().map(p -> {
            SalaryEmployee emp = empMap.get(p.getEmployeeId());
            SalarySummary summary = new SalarySummary();
            summary.setEmployeeId(p.getEmployeeId());
            summary.setPeriodId(p.getId());
            summary.setSettlementMonth(month);

            if (emp != null) {
                summary.setEmployeeCode(emp.getEmployeeCode());
                summary.setEmployeeName(emp.getEmployeeName());
            } else {
                summary.setEmployeeCode("UNKNOWN");
                summary.setEmployeeName("未知");
            }
            // 初始状态设定
            summary.setCalcStatus(0);
            summary.setPaymentStatus(0);
            summary.setLockFlag(0);
            summary.setCalcVersion(1);

            // 金额清零
            summary.setIncomeTotal(BigDecimal.ZERO);
            summary.setDeductionTotal(BigDecimal.ZERO);
            summary.setTaxTotal(BigDecimal.ZERO);
            summary.setGrossSalary(BigDecimal.ZERO);
            summary.setNetSalary(BigDecimal.ZERO);

            return summary;
        }).collect(Collectors.toList());

        return iSalarySummaryService.saveBatch(newSummaries, 500);
    }
    /**
     * 🌟 新增：单人核算数据实时预览 (仅内存计算，不落库)
     * 逻辑与 calculateEmployeeSalary 高度复用，但剥离了持久化操作
     */
    @Override
    public SalarySummaryVO previewCalculate(SalaryCalcSingleReqDTO reqDTO) {
        // 0. 获取原始账套
        SalarySummary summary = iSalarySummaryService.getById(reqDTO.getSummaryId());
        if (summary == null){
            throw new BusinessException("薪资账套数据不存在！");
        }

        Long periodId = summary.getPeriodId();
        Long employeeId = summary.getEmployeeId();
        String pipelineCode = StringUtils.isNotBlank(reqDTO.getPipelineCode()) ? reqDTO.getPipelineCode() : "OFFICIAL_STAFF_2026";
        Integer pipelineVersion = reqDTO.getPipelineVersion() != null ? reqDTO.getPipelineVersion() : 1;

        log.info("🔍 [引擎预览] 周期: [{}], 员工: [{}], 管道: [{}-V{}]", periodId, employeeId, pipelineCode, pipelineVersion);

        // 1. 获取图纸
        List<SalaryCalcPipelineStep> steps = iSalaryCalcPipelineStepService.lambdaQuery()
                .eq(SalaryCalcPipelineStep::getPipelineCode, pipelineCode)
                .eq(SalaryCalcPipelineStep::getPipelineVersion, pipelineVersion)
                .eq(SalaryCalcPipelineStep::getStatus, 1)
                .orderByAsc(SalaryCalcPipelineStep::getStage)
                .orderByAsc(SalaryCalcPipelineStep::getSortOrder)
                .list();

        if (steps.isEmpty()) throw new BusinessException("薪资管道未配置有效的核算步骤！");

        // 2. 准备原材料
        Map<String, Object> env = iSalaryCalcContextService.buildEmployeeContext(periodId, employeeId,pipelineCode,pipelineVersion);
        // 提取埋点注入的所用档案列表，用于审计溯源和UI展示
        List<ArchiveSnapshot> usedArchives =
                (List<ArchiveSnapshot>) env.get("_usedArchives");

        // 提前全量查询字典配置，用于精准分类
        Set<String> ruleCodes = steps.stream().map(SalaryCalcPipelineStep::getRuleCode).collect(Collectors.toSet());
        Map<String, SalaryItemConfig> itemConfigMap = iSalaryItemConfigService.lambdaQuery()
                .in(SalaryItemConfig::getItemCode, ruleCodes)
                .list()
                .stream()
                .collect(Collectors.toMap(SalaryItemConfig::getItemCode, c -> c, (v1, v2) -> v1));
        // 初始化累加器与快照
        BigDecimal incomeTotal = BigDecimal.ZERO;
        BigDecimal deductionTotal = BigDecimal.ZERO;
        BigDecimal taxTotal = BigDecimal.ZERO;

        SalarySnapshotDTO snapshot = new SalarySnapshotDTO();
        snapshot.setIncome(new ArrayList<>());
        snapshot.setDeduction(new ArrayList<>());
        snapshot.setTax(new ArrayList<>());

        // 3. 驱动流水线 (纯内存试算)
        for (SalaryCalcPipelineStep step : steps) {
            String ruleCode = step.getRuleCode();
            try {
                if (StringUtils.isNotBlank(step.getConditionScript())) {
                    Boolean shouldRun = (Boolean) AviatorEvaluator.execute(step.getConditionScript(), env);
                    if (!shouldRun) {
                        env.put(ruleCode, BigDecimal.ZERO);
                        continue;
                    }
                }

                CalcRuleVO rule = iSalaryCalcRuleService.getByRuleCode(ruleCode);
                BigDecimal stepResult = salaryRuleEngine.execute(rule.getRuleScript(), env);

                if (step.getSkipIfNull() == 1 && stepResult.compareTo(BigDecimal.ZERO) == 0) {
                    env.put(ruleCode, BigDecimal.ZERO);
                    continue;
                }

                env.put(ruleCode, stepResult);
                // 🌟 【架构师优化】：抛弃按 Stage 死板判断分类的做法，直接取配置表的真实属性
                SalaryItemConfig config = itemConfigMap.get(ruleCode);
                int itemType = config != null ? config.getItemCategory() : 1;

                SalaryDetailItemDTO snapshotItem = SalaryDetailItemDTO.builder()
                        .itemCode(ruleCode)
                        .itemName(step.getRuleName())
                        .settlementAmount(stepResult)
                        .source("SYSTEM_CALC_PREVIEW")
                        .calcLog("公式规则: " + ruleCode)
                        .sort(step.getSortOrder())
                        .build();

                switch (itemType) {
                    case 1: snapshot.getIncome().add(snapshotItem); incomeTotal = incomeTotal.add(stepResult); break;
                    case 2: snapshot.getDeduction().add(snapshotItem); deductionTotal = deductionTotal.add(stepResult); break;
                    case 3: snapshot.getTax().add(snapshotItem); taxTotal = taxTotal.add(stepResult); break;
                }
            } catch (Exception e) {
                if (step.getBlockFlag() == 1) throw new BusinessException("预览中断: [" + ruleCode + "] 异常 - " + e.getMessage());
                env.put(ruleCode, BigDecimal.ZERO);
            }
        }

        // 4. 组装展示视图 (无需 JSON 序列化和 DB 更新)
        BigDecimal netSalary = incomeTotal.subtract(deductionTotal).subtract(taxTotal);

        SalarySummaryVO previewVO = new SalarySummaryVO();
        previewVO.setId(summary.getId());
        previewVO.setEmployeeName(summary.getEmployeeName());
        previewVO.setSettlementMonth(summary.getSettlementMonth());
        previewVO.setGrossSalary(incomeTotal);
        previewVO.setDeductionTotal(deductionTotal);
        previewVO.setTaxTotal(taxTotal);
        previewVO.setNetSalary(netSalary);

        // 向前端 VO 注入档案溯源快照
        previewVO.setUsedArchives(usedArchives);
        // 挂载明细用于更深度的前端展示（如果需要）
        snapshot.setGrossSalary(incomeTotal);
        snapshot.setDeductionTotal(deductionTotal);
        snapshot.setTaxTotal(taxTotal);
        snapshot.setNetSalary(netSalary);
        previewVO.setDetails(snapshot);

        return previewVO;
    }
    /**
     * 执行单人当月薪资核算 (瀑布流管道计算核心)
     *
     * @param reqDTO 单人核算参数指令
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void calculateEmployeeSalary(SalaryCalcSingleReqDTO reqDTO) {
        // 0. 参数补全逻辑 (如果直接调单人核算API，这里自动补全)
        if (reqDTO.getPeriodId() == null || reqDTO.getEmployeeId() == null) {
            SalarySummary summary = iSalarySummaryService.getById(reqDTO.getSummaryId());
            if (summary == null) throw new BusinessException("薪资账套数据不存在！");
            if (summary.getLockFlag() == 1) throw new BusinessException("该工资单已锁定，禁止重算！");

            reqDTO.setPeriodId(summary.getPeriodId());
            reqDTO.setEmployeeId(summary.getEmployeeId());
        }

        Long periodId = reqDTO.getPeriodId();
        Long employeeId = reqDTO.getEmployeeId();

        // 管道降级策略：如果前端未指定管道，默认使用 V1 正式管道
        String pipelineCode = StringUtils.isNotBlank(reqDTO.getPipelineCode()) ? reqDTO.getPipelineCode() : "OFFICIAL_STAFF_2026";
        Integer pipelineVersion = reqDTO.getPipelineVersion() != null ? reqDTO.getPipelineVersion() : 1;

        log.info("▶️ [引擎启动] 周期: [{}], 员工: [{}], 管道: [{}-V{}]", periodId, employeeId, pipelineCode, pipelineVersion);

        // 1. 获取图纸 (按 stage 和 sortOrder 排序)
        List<SalaryCalcPipelineStep> steps = iSalaryCalcPipelineStepService.lambdaQuery()
                .eq(SalaryCalcPipelineStep::getPipelineCode, pipelineCode)
                .eq(SalaryCalcPipelineStep::getPipelineVersion, pipelineVersion)
                .eq(SalaryCalcPipelineStep::getStatus, 1)
                .orderByAsc(SalaryCalcPipelineStep::getStage)
                .orderByAsc(SalaryCalcPipelineStep::getSortOrder)
                .list();

        if (steps.isEmpty()) {
            throw new BusinessException("薪资管道 [" + pipelineCode + "-V" + pipelineVersion + "] 未配置有效的核算步骤！");
        }

        // 2. 准备原材料 (Context Env)
        Map<String, Object> env = iSalaryCalcContextService.buildEmployeeContext(periodId, employeeId,pipelineCode,pipelineVersion);
        // 🌟 【架构师优化】：提取档案溯源快照
        List<ArchiveSnapshot> usedArchives =
                (List<ArchiveSnapshot>) env.get("_usedArchives");
        // 初始化数据载体
        List<SalaryItemDetail> detailList = new ArrayList<>();

        // 初始化 JSON Snapshot (防空指针)
        SalarySnapshotDTO snapshot = new SalarySnapshotDTO();
        snapshot.setIncome(new ArrayList<>());
        snapshot.setDeduction(new ArrayList<>());
        snapshot.setTax(new ArrayList<>());
        snapshot.setCompanyExpense(new ArrayList<>());
        // 注入档案快照到落库 JSON 中，彻底解决财务审计的后顾之忧
        snapshot.setUsedArchives(usedArchives);
        // 初始化累加器
        BigDecimal incomeTotal = BigDecimal.ZERO;
        BigDecimal deductionTotal = BigDecimal.ZERO;
        BigDecimal taxTotal = BigDecimal.ZERO;
        BigDecimal companyExpenseTotal = BigDecimal.ZERO;
        // 在进循环前，根据流水线将要执行的 ruleCode，提前把字典配置里的 ID 查出来放入 Map 中
        Set<String> ruleCodes = steps.stream().map(SalaryCalcPipelineStep::getRuleCode).collect(Collectors.toSet());
        // 将 Map 的泛型改为 SalaryItemConfig 实体
        Map<String, SalaryItemConfig> itemConfigMap = iSalaryItemConfigService.lambdaQuery()
                .in(SalaryItemConfig::getItemCode, ruleCodes)
                .list()
                .stream()
                // 将 value 映射为 c -> c (即实体本身)，而不是 SalaryItemConfig::getId
                .collect(Collectors.toMap(SalaryItemConfig::getItemCode, c -> c, (v1, v2) -> v1));
        // 3. 驱动流水线
        for (SalaryCalcPipelineStep step : steps) {
            //  记录开始时间
            long startTime = System.currentTimeMillis();
            BigDecimal stepResult = null;
            String errorMsg = null;
            String ruleCode = step.getRuleCode();

            try {
                // 3.1 前置拦截 (Condition Script)
                if (StringUtils.isNotBlank(step.getConditionScript())) {
                    Boolean shouldRun = (Boolean) AviatorEvaluator.execute(step.getConditionScript(), env);
                    if (!shouldRun) {
                        env.put(ruleCode, BigDecimal.ZERO);
                        continue; // 条件不满足，跳过该规则执行
                    }
                }

                // 3.2 查规则脚本
                CalcRuleVO rule = iSalaryCalcRuleService.getByRuleCode(ruleCode);

                // 3.3 Aviator 核心计算
                 stepResult = salaryRuleEngine.execute(rule.getRuleScript(), env);

                // 3.4 空值跳过
                if (step.getSkipIfNull() == 1 && stepResult.compareTo(BigDecimal.ZERO) == 0) {
                    env.put(ruleCode, BigDecimal.ZERO);
                    continue;
                }

                env.put(ruleCode, stepResult);
                SalaryItemConfig config = itemConfigMap.get(ruleCode);
                Long itemConfigId = config != null ? config.getId() : 0L;
                int itemType = config != null ? config.getItemCategory() : 1;
                SalaryItemDetail detail = new SalaryItemDetail()
                        .setPeriodId(periodId)
                        .setEmployeeId(employeeId)
                        .setSummaryId(reqDTO.getSummaryId()) // 关联主表ID
                        .setItemConfigId(itemConfigId)
                        .setItemCode(ruleCode)
                        .setItemName(step.getRuleName())
                        .setSettlementAmount(stepResult)
                        .setSettlementCurrency("CNY")
                        .setItemType(itemType)
                        .setSourceType(2) // 2-引擎计算
                        .setCalcPriority(step.getSortOrder());
                detailList.add(detail);

                // 组装 JSON 快照 DTO
                SalaryDetailItemDTO snapshotItem = SalaryDetailItemDTO.builder()
                        .itemCode(ruleCode)
                        .itemName(step.getRuleName())
                        .settlementAmount(stepResult)
                        .source("SYSTEM_CALC")
                        .calcLog("公式规则: " + ruleCode) // 记录日志快照
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
                    default:
                        break;
                }

            } catch (Exception e) {
                log.error("❌ 节点: {} 计算失败！原因: {}", ruleCode, e.getMessage());
                if (step.getBlockFlag() == 1) {
                    // 如果阻断开关开启，则抛出异常，中断整个个体的核算并回滚
                    throw new BusinessException("核算中断: [" + ruleCode + "] 计算异常 - " + e.getMessage());
                } else {
                    // 容错模式：跳过并记为 0
                    env.put(ruleCode, BigDecimal.ZERO);
                }
            }finally {
                // 无论计算成功还是失败，都在 finally 块中推送异步审计日志
                long executeTime = System.currentTimeMillis() - startTime;

                SalaryCalcLog auditLog = new SalaryCalcLog();
                auditLog.setEmployeeId(employeeId);
                auditLog.setPeriodId(periodId);
                auditLog.setRuleCode(ruleCode);
                auditLog.setStage(step.getStage() != null ? step.getStage() : 1);
                auditLog.setInputJson(JSONUtil.toJsonStr(env)); // 保存当时的计算环境变量
                auditLog.setOutputValue(stepResult);
                auditLog.setErrorMsg(errorMsg);
                auditLog.setExecuteTime(executeTime);

                // 发布事件 (交由后台线程异步落库)
                eventPublisher.publishEvent(new CalcLogEvent(auditLog));
            }
        }

        // =========================================================================
        // 4. 成品包装：保存明细与主表汇总
        // =========================================================================

        // 4.1 删除历史明细 (支持重算覆写)
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
            // 记录核心引擎环境变量作为排查依据
            snapshot.setCalcRemark("引擎计算成功，使用的管道: " + pipelineCode);
            // 把 env 里的基础数据拿出来，真正塞进快照里！
            snapshot.setAttendanceDays((BigDecimal) env.getOrDefault("attendanceDays", BigDecimal.ZERO));
            snapshot.setMonthDays((BigDecimal) env.getOrDefault("monthDays", BigDecimal.ZERO));
            snapshot.setBaseSalary((BigDecimal) env.getOrDefault("baseSalary", BigDecimal.ZERO));
            // 将 DTO 序列化为 JSON 字符串
            String detailJsonString = JSONUtil.toJsonStr(snapshot);

            // 更新 Summary 主表
            summary.setIncomeTotal(incomeTotal)
                    .setDeductionTotal(deductionTotal)
                    .setTaxTotal(taxTotal)
                    .setGrossSalary(grossSalary)
                    .setNetSalary(netSalary)
                    .setCalcStatus(1) // 1-计算成功
                    .setDetailJson(detailJsonString); // ：注入结构化工资条快照

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
        // 批量查出单据
        List<SalarySummary> summaryList = iSalarySummaryService.listByIds(reqDTO.getSummaryIds());
        if (CollUtil.isEmpty(summaryList)) {
            throw new BusinessException("未找到匹配的核算账套数据！");
        }

        int successCount = 0;
        int failCount = 0;

        for (SalarySummary summary : summaryList) {
            // 核心拦截：已锁定的工资单绝对禁止重新核算
            if (summary.getLockFlag() == 1) {
                log.warn("⚠️ 员工 [{}] 的工资单已锁定，已跳过引擎重算。", summary.getEmployeeName());
                continue;
            }

            try {
                // 利用 @Accessors(chain = true) 优雅地构造单人请求
                SalaryCalcSingleReqDTO singleReq = new SalaryCalcSingleReqDTO()
                        .setSummaryId(summary.getId())
                        .setPeriodId(summary.getPeriodId())
                        .setEmployeeId(summary.getEmployeeId())
                        .setPipelineCode(reqDTO.getPipelineCode())
                        .setPipelineVersion(reqDTO.getPipelineVersion());

                // 调用单人核算 (内层包含独立事务控制)
                this.calculateEmployeeSalary(singleReq);
                successCount++;
            } catch (Exception e) {
                // 异常隔离：批量处理时，其中一人报错，不影响其他人的计算进度
                log.error("❌ 员工 [{}] 核算失败: {}", summary.getEmployeeName(), e.getMessage());

                // 将失败状态写回主表
                summary.setCalcStatus(2); // 2-计算失败
                summary.setRemark("核算报错: " + e.getMessage());
                iSalarySummaryService.updateById(summary);
                failCount++;
            }
        }

        log.info("📊 批量核算任务结束。成功: {} 条，失败: {} 条", successCount, failCount);
        if (failCount > 0) {
            throw new BusinessException("核算结束。成功 " + successCount + " 条，但有 " + failCount + " 条发生异常，请查看详情。");
        }
    }

    /**
     * 辅助方法：根据核算阶段映射项目收支类型
     */
    private int determineItemTypeByStage(Integer stage) {
        if (stage == null) return 1;
        // 1-收入, 2-扣款, 3-税费, 4-公司支出/补贴
        return switch (stage) { // 基础薪资阶段
            case 1, 2 -> // 津贴与奖金阶段
                    1; // 收入
            case 3 -> // 扣款阶段
                    2; // 扣款
            case 4 -> // 税务阶段
                    3; // 税费
            case 5 -> // 汇总结算阶段
                    4; // 支出或其他
            default -> 1;
        };
    }
}