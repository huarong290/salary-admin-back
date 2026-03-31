package com.salary.admin.service.impl.salary;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.salary.admin.engine.SalaryRuleEngine;
import com.salary.admin.model.dto.salary.period.PeriodBatchInitReqDTO;
import com.salary.admin.model.dto.salary.snapshot.SalaryDetailItemDTO;
import com.salary.admin.model.dto.salary.snapshot.SalarySnapshotDTO;
import com.salary.admin.model.entity.salary.*;
import com.salary.admin.model.vo.salary.archive.SalaryArchiveVO;
import com.salary.admin.model.vo.salary.period.PeriodBatchInitResultVO;
import com.salary.admin.service.salary.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 薪资核心引擎实现类 (V3.0 管道引擎最终版)
 * <p>
 * 核心定位：
 * - 薪资模块的“总调度室”，负责跨表、跨业务的复杂逻辑编排。
 * - 协调 Archive(档案)、Pipeline(计算管道)、Detail(统一明细) 与 Summary(汇总)。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SalaryCoreEngineImpl implements ISalaryCoreEngine {

    // ============================
    // 基础业务 Service
    // ============================
    private final ISalaryPeriodService iSalaryPeriodService;
    private final ISalarySummaryService iSalarySummaryService;
    private final ISalaryArchiveService iSalaryArchiveService;
    private final ISalaryArchiveItemService iSalaryArchiveItemService;
    private final ISalaryItemConfigService iSalaryItemConfigService;
    private final ISalaryEmployeeService iSalaryEmployeeService;

    // ============================
    // 计算引擎核心 Service
    // ============================
    private final SalaryRuleEngine salaryRuleEngine;
    private final ISalaryCalcPipelineService iSalaryCalcPipelineService;
    private final ISalaryCalcRuleService iSalaryCalcRuleService;
    private final ISalaryCalcContextService iSalaryCalcContextService;
    private final ISalaryCalcLogService iSalaryCalcLogService;
    private final ISalaryItemDetailService iSalaryItemDetailService; // 统一明细表


    // ==========================================
    // 1. 周期与汇总初始化 (账套准备)
    // ==========================================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PeriodBatchInitResultVO batchInitPeriods(PeriodBatchInitReqDTO reqDTO) {
        log.info("🚀 [薪资引擎] 开始执行 {} 月份批量初始化账套...", reqDTO.getSettlementMonth());
        PeriodBatchInitResultVO resultVO = iSalaryPeriodService.batchInitPeriodsOnly(reqDTO);

        if (CollUtil.isNotEmpty(resultVO.getNewPeriodEntities())) {
            this.initSummaryForPeriods(resultVO.getNewPeriodEntities());
            log.info("✅ 已联动初始化 {} 条汇总单 (Summary)", resultVO.getNewPeriodEntities().size());
        }
        return resultVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initMonthlyBatchForAll(String settlementMonth) {
        log.info("🚀 [薪资引擎] 开始执行 {} 月份全员月度建账...", settlementMonth);

        List<SalaryEmployee> activeEmployees = iSalaryEmployeeService.list(
                Wrappers.<SalaryEmployee>lambdaQuery()
                        .eq(SalaryEmployee::getEmploymentStatus, 1)
                        .eq(SalaryEmployee::getDeleteFlag, 0)
        );
        if (CollUtil.isEmpty(activeEmployees)) return;

        List<Long> allActiveIds = activeEmployees.stream().map(SalaryEmployee::getId).collect(Collectors.toList());
        LocalDate start = DateUtil.beginOfMonth(DateUtil.parse(settlementMonth, "yyyyMM")).toLocalDateTime().toLocalDate();
        LocalDate end = DateUtil.endOfMonth(DateUtil.parse(settlementMonth, "yyyyMM")).toLocalDateTime().toLocalDate();

        List<Long> existPeriodEmpIds = iSalaryPeriodService.list(
                Wrappers.<SalaryPeriod>lambdaQuery()
                        .select(SalaryPeriod::getEmployeeId)
                        .eq(SalaryPeriod::getSettlementMonth, settlementMonth)
                        .in(SalaryPeriod::getEmployeeId, allActiveIds)
        ).stream().map(SalaryPeriod::getEmployeeId).collect(Collectors.toList());

        List<Long> needCreateIds = allActiveIds.stream()
                .filter(id -> !existPeriodEmpIds.contains(id))
                .collect(Collectors.toList());

        if (CollUtil.isNotEmpty(needCreateIds)) {
            PeriodBatchInitReqDTO initReq = new PeriodBatchInitReqDTO();
            initReq.setSettlementMonth(settlementMonth);
            initReq.setEmployeeIds(needCreateIds);
            initReq.setStartDate(start);
            initReq.setEndDate(end);
            this.batchInitPeriods(initReq);
        }

        // 防御性补全缺失的汇总单
        List<SalaryPeriod> allPeriods = iSalaryPeriodService.list(
                Wrappers.<SalaryPeriod>lambdaQuery().eq(SalaryPeriod::getSettlementMonth, settlementMonth)
        );
        List<SalaryPeriod> periodsWithoutSummary = allPeriods.stream().filter(p ->
                !iSalarySummaryService.exists(Wrappers.<SalarySummary>lambdaQuery().eq(SalarySummary::getPeriodId, p.getId()))
        ).collect(Collectors.toList());

        if (CollUtil.isNotEmpty(periodsWithoutSummary)) {
            this.initSummaryForPeriods(periodsWithoutSummary);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initSummaryForPeriods(List<SalaryPeriod> periods) {
        if (CollectionUtils.isEmpty(periods)) return;

        List<Long> employeeIds = periods.stream().map(SalaryPeriod::getEmployeeId).distinct().collect(Collectors.toList());
        Map<Long, SalaryEmployee> employeeMap = iSalaryEmployeeService.listByIds(employeeIds).stream()
                .collect(Collectors.toMap(SalaryEmployee::getId, e -> e));

        List<SalarySummary> summaries = periods.stream().map(p -> {
            SalaryEmployee emp = employeeMap.get(p.getEmployeeId());
            SalarySummary s = new SalarySummary();
            s.setEmployeeId(p.getEmployeeId());
            s.setEmployeeCode(emp != null ? emp.getEmployeeCode() : "");
            s.setEmployeeName(emp != null ? emp.getEmployeeName() : "未知员工");
            s.setPeriodId(p.getId());
            s.setSettlementMonth(p.getSettlementMonth());
            s.setPeriodStartDate(p.getStartDate());
            s.setPeriodEndDate(p.getEndDate());
            s.setIncomeTotal(BigDecimal.ZERO);
            s.setDeductionTotal(BigDecimal.ZERO);
            s.setTaxTotal(BigDecimal.ZERO);
            s.setGrossSalary(BigDecimal.ZERO);
            s.setNetSalary(BigDecimal.ZERO);
            s.setPaymentStatus(0);
            s.setCalcStatus(0);
            return s;
        }).collect(Collectors.toList());

        iSalarySummaryService.saveBatch(summaries);
    }


    // ==========================================
    // 2. 算薪调度器 (Orchestrators)
    // ==========================================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeGlobalSettlement(String settlementMonth) {
        log.info("🚀 [薪资引擎] 开始执行 {} 月份全员核算任务", settlementMonth);
        this.initMonthlyBatchForAll(settlementMonth);

        List<SalaryPeriod> periods = iSalaryPeriodService.list(
                Wrappers.<SalaryPeriod>lambdaQuery().eq(SalaryPeriod::getSettlementMonth, settlementMonth)
        );
        if (CollUtil.isEmpty(periods)) return;

        int successCount = 0;
        for (SalaryPeriod period : periods) {
            try {
                SalarySummary summary = iSalarySummaryService.getOne(Wrappers.<SalarySummary>lambdaQuery().eq(SalarySummary::getPeriodId, period.getId()));
                if (summary == null || summary.getLockFlag() == 1) continue; // 锁定状态禁止重算

                SalaryArchiveVO archive = this.matchArchiveByTimeSlice(period.getEmployeeId(), period.getSettlementMonth());
                if (archive == null) continue;

                this.createRecordByCalculation(summary.getId(), archive, period);
                successCount++;
            } catch (Exception e) {
                log.error("❌ 员工ID {} 核算异常: {}", period.getEmployeeId(), e.getMessage());
            }
        }
        log.info("✅ {} 月份全员核算完毕！成功 {} 人", settlementMonth, successCount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeSettlementByPeriods(List<Long> periodIds) {
        if (CollUtil.isEmpty(periodIds)) return;

        List<SalaryPeriod> periods = iSalaryPeriodService.listByIds(periodIds);
        for (SalaryPeriod period : periods) {
            try {
                SalarySummary summary = iSalarySummaryService.getOne(Wrappers.<SalarySummary>lambdaQuery().eq(SalarySummary::getPeriodId, period.getId()));
                if (summary == null || summary.getLockFlag() == 1) continue;

                SalaryArchiveVO archive = this.matchArchiveByTimeSlice(period.getEmployeeId(), period.getSettlementMonth());
                if (archive == null) continue;

                this.createRecordByCalculation(summary.getId(), archive, period);
            } catch (Exception e) {
                log.error("❌ 员工ID {} 核算异常: {}", period.getEmployeeId(), e.getMessage());
            }
        }
    }


    // ==========================================
    // 3. 🚀 核心心脏：单人流水线核算 (Pipeline Execution)
    // ==========================================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRecordByCalculation(Long summaryId, SalaryArchiveVO archive, SalaryPeriod period) {
        long engineStartTime = System.currentTimeMillis();

        // 1. 幂等性清理
        iSalaryItemDetailService.remove(Wrappers.<SalaryItemDetail>lambdaQuery().eq(SalaryItemDetail::getSummaryId, summaryId));
        iSalaryCalcLogService.remove(Wrappers.<SalaryCalcLog>lambdaQuery().eq(SalaryCalcLog::getPeriodId, period.getId()));
        iSalaryCalcContextService.remove(Wrappers.<SalaryCalcContext>lambdaQuery().eq(SalaryCalcContext::getPeriodId, period.getId()));

        // 2. 初始化全局上下文
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("baseSalary", archive.getBaseSalary() != null ? archive.getBaseSalary() : BigDecimal.ZERO);
        ctx.put("monthDays", period.getMonthDays() != null ? period.getMonthDays() : new BigDecimal("21.75"));
        ctx.put("attendanceDays", period.getAttendanceDays() != null ? period.getAttendanceDays() : BigDecimal.ZERO);
        ctx.put("taxRuleCode", archive.getTaxRuleCode());
        // 确保布尔值注入引擎
        ctx.put("isFullAttendance", Integer.valueOf(1).equals(period.getFullAttendanceFlag()));

        Map<String, SalaryItemConfig> itemConfigMap = iSalaryItemConfigService.list().stream()
                .collect(Collectors.toMap(SalaryItemConfig::getItemCode, c -> c));

        List<SalaryItemDetail> allDetails = new ArrayList<>();

        // 3. 预处理：加载档案固定项
        List<SalaryArchiveItem> archiveItems = iSalaryArchiveItemService.list(
                Wrappers.<SalaryArchiveItem>lambdaQuery().eq(SalaryArchiveItem::getArchiveId, archive.getId())
        );
        for (SalaryArchiveItem aItem : archiveItems) {
            SalaryItemConfig config = iSalaryItemConfigService.getById(aItem.getItemConfigId());
            if (config != null) {
                BigDecimal amount = aItem.getAmount() != null ? aItem.getAmount() : BigDecimal.ZERO;
                ctx.put(config.getEnvVarName(), amount);
                SalaryItemDetail detail = buildItemDetail(period, summaryId, config, amount, 1, "从员工档案直接引入");
                detail.setArchiveId(archive.getId());
                detail.setArchiveItemId(aItem.getId());
                allDetails.add(detail);
            }
        }

        // 4. 加载执行管道与规则库
        List<SalaryCalcPipeline> pipelines = iSalaryCalcPipelineService.list(
                Wrappers.<SalaryCalcPipeline>lambdaQuery().eq(SalaryCalcPipeline::getPipelineCode, "DEFAULT_PIPELINE").eq(SalaryCalcPipeline::getStatus, 1)
                        .orderByAsc(SalaryCalcPipeline::getStage, SalaryCalcPipeline::getSortOrder)
        );
        Map<String, SalaryCalcRule> ruleMap = iSalaryCalcRuleService.list(
                Wrappers.<SalaryCalcRule>lambdaQuery().eq(SalaryCalcRule::getStatus, 1)
        ).stream().collect(Collectors.toMap(SalaryCalcRule::getRuleCode, r -> r));

        List<SalaryCalcLog> calcLogs = new ArrayList<>();

        // 5. 🚀 引擎轰鸣：顺次执行 Pipeline
        for (SalaryCalcPipeline step : pipelines) {
            long stepStartTime = System.currentTimeMillis();
            SalaryCalcRule rule = ruleMap.get(step.getRuleCode());
            if (rule == null) continue;

            BigDecimal resultValue = BigDecimal.ZERO;
            String errorMsg = null;

            try {
                resultValue = salaryRuleEngine.execute(rule.getRuleScript(), ctx);
                ctx.put(rule.getRuleCode(), resultValue); // 瀑布流反写上下文

                SalaryItemConfig config = itemConfigMap.get(rule.getRuleCode());
                if (config != null && resultValue.compareTo(BigDecimal.ZERO) != 0) {
                    SalaryItemDetail detail = buildItemDetail(period, summaryId, config, resultValue, 2, "引擎计算生成");
                    detail.setRuleCode(rule.getRuleCode());
                    detail.setCalcSnapshot(JSON.toJSONString(ctx));
                    detail.setCalcPriority(step.getSortOrder());
                    allDetails.add(detail);
                }
            } catch (Exception e) {
                errorMsg = e.getMessage();
                log.error("Pipeline 规则执行异常! Code: {}", rule.getRuleCode(), e);
                throw new RuntimeException("薪资核算中断于规则 [" + rule.getRuleName() + "]: " + e.getMessage());
            } finally {
                SalaryCalcLog logEntity = new SalaryCalcLog();
                logEntity.setEmployeeId(period.getEmployeeId());
                logEntity.setPeriodId(period.getId());
                logEntity.setRuleCode(rule.getRuleCode());
                logEntity.setStage(step.getStage());
                logEntity.setInputJson(JSON.toJSONString(ctx));
                logEntity.setOutputValue(resultValue);
                logEntity.setErrorMsg(errorMsg);
                logEntity.setExecuteTime(System.currentTimeMillis() - stepStartTime);
                calcLogs.add(logEntity);
            }
        }

        // 6. 财务汇总与数据归集
        BigDecimal grossSalary = BigDecimal.ZERO;
        BigDecimal deductionTotal = BigDecimal.ZERO;
        BigDecimal taxTotal = BigDecimal.ZERO;
        List<SalaryDetailItemDTO> snapshotItems = new ArrayList<>();

        for (SalaryItemDetail d : allDetails) {
            if (d.getItemType() == 1) grossSalary = grossSalary.add(d.getSettlementAmount());
            else if (d.getItemType() == 2) deductionTotal = deductionTotal.add(d.getSettlementAmount());
            else if (d.getItemType() == 3) taxTotal = taxTotal.add(d.getSettlementAmount());

            snapshotItems.add(SalaryDetailItemDTO.builder()
                    .itemCode(d.getItemCode())
                    .itemName(d.getItemName())
                    .settlementAmount(d.getSettlementAmount())
                    .categoryDictValue(d.getCategoryDictValue())
                    .source(d.getSourceType() == 1 ? "FIXED" : "SYSTEM_CALC")
                    .calcLog(d.getRuleCode() != null ? "规则:" + d.getRuleCode() : "固定引入")
                    .sort(d.getCalcPriority())
                    .build());
        }

        BigDecimal netSalary = grossSalary.subtract(deductionTotal).subtract(taxTotal);
        if (netSalary.compareTo(BigDecimal.ZERO) < 0) netSalary = BigDecimal.ZERO;

        // 7. 持久化落盘 (DB 写入)
        SalaryCalcContext contextEntity = new SalaryCalcContext();
        contextEntity.setEmployeeId(period.getEmployeeId());
        contextEntity.setPeriodId(period.getId());
        contextEntity.setPipelineCode("DEFAULT_PIPELINE");
        contextEntity.setContextJson(JSON.toJSONString(ctx));
        iSalaryCalcContextService.save(contextEntity);

        if (CollUtil.isNotEmpty(calcLogs)) iSalaryCalcLogService.saveBatch(calcLogs);
        if (CollUtil.isNotEmpty(allDetails)) iSalaryItemDetailService.saveBatch(allDetails);

        SalarySnapshotDTO snapshot = new SalarySnapshotDTO();
        snapshot.setMonthDays((BigDecimal) ctx.get("monthDays"));
        snapshot.setAttendanceDays((BigDecimal) ctx.get("attendanceDays"));
        snapshot.setBaseSalary((BigDecimal) ctx.get("baseSalary"));
        snapshot.setSettlementCurrency(archive.getCurrency());
        snapshot.setExchangeRate(BigDecimal.ONE);
        snapshot.setGrossSalary(grossSalary);
        snapshot.setDeductionTotal(deductionTotal);
        snapshot.setTaxTotal(taxTotal);
        snapshot.setNetSalary(netSalary);

        snapshot.setIncome(snapshotItems.stream().filter(i -> i.getSort() < 100).collect(Collectors.toList()));
        snapshot.setDeduction(snapshotItems.stream().filter(i -> i.getSort() >= 100 && i.getSort() < 200).collect(Collectors.toList()));
        snapshot.setTax(snapshotItems.stream().filter(i -> i.getSort() >= 200).collect(Collectors.toList()));

        SalarySummary updateSummary = new SalarySummary();
        updateSummary.setId(summaryId);
        updateSummary.setIncomeTotal(grossSalary);
        updateSummary.setGrossSalary(grossSalary);
        updateSummary.setDeductionTotal(deductionTotal);
        updateSummary.setTaxTotal(taxTotal);
        updateSummary.setNetSalary(netSalary);
        updateSummary.setCalcStatus(1);
        updateSummary.setDetailJson(JSON.toJSONString(snapshot));

        iSalarySummaryService.updateById(updateSummary);
        return summaryId;
    }


    // ==========================================
    // 4. 数据干预与同步 (Manual & Sync)
    // ==========================================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRecordByManual(Long summaryId, Long employeeId, BigDecimal finalAmount, String remark) {
        // V3.0 下，手动调整也是插入一条 ItemDetail
        SalarySummary summary = iSalarySummaryService.getById(summaryId);
        SalaryPeriod period = iSalaryPeriodService.getById(summary.getPeriodId());

        // 清空之前算好的引擎明细
        iSalaryItemDetailService.remove(Wrappers.<SalaryItemDetail>lambdaQuery().eq(SalaryItemDetail::getSummaryId, summaryId));

        SalaryItemDetail manualDetail = new SalaryItemDetail();
        manualDetail.setEmployeeId(employeeId);
        manualDetail.setPeriodId(period.getId());
        manualDetail.setSummaryId(summaryId);
        manualDetail.setItemType(1); // 默认作为一笔大额收入
        manualDetail.setItemConfigId(0L);
        manualDetail.setItemCode("MANUAL_ADJUST");
        manualDetail.setItemName("人工核定实发");
        manualDetail.setSourceType(3); // 3-手动调整
        manualDetail.setOriginalAmount(finalAmount);
        manualDetail.setSettlementAmount(finalAmount);
        manualDetail.setRemark(remark);
        iSalaryItemDetailService.save(manualDetail);

        this.refreshSummaryAmountBySummaryId(summaryId);
        return summaryId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncSummaryAmountByPeriodId(Long periodId) {
        SalarySummary summary = iSalarySummaryService.getOne(Wrappers.<SalarySummary>lambdaQuery().eq(SalarySummary::getPeriodId, periodId));
        if (summary != null) {
            this.refreshSummaryAmountBySummaryId(summary.getId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refreshSummaryAmountBySummaryId(Long summaryId) {
        List<SalaryItemDetail> details = iSalaryItemDetailService.list(
                Wrappers.<SalaryItemDetail>lambdaQuery().eq(SalaryItemDetail::getSummaryId, summaryId)
        );

        BigDecimal gross = BigDecimal.ZERO;
        BigDecimal deduction = BigDecimal.ZERO;
        BigDecimal tax = BigDecimal.ZERO;

        for (SalaryItemDetail d : details) {
            if (d.getItemType() == 1) gross = gross.add(d.getSettlementAmount());
            else if (d.getItemType() == 2) deduction = deduction.add(d.getSettlementAmount());
            else if (d.getItemType() == 3) tax = tax.add(d.getSettlementAmount());
        }

        BigDecimal net = gross.subtract(deduction).subtract(tax);
        if (net.compareTo(BigDecimal.ZERO) < 0) net = BigDecimal.ZERO;

        SalarySummary updateSummary = new SalarySummary();
        updateSummary.setId(summaryId);
        updateSummary.setIncomeTotal(gross);
        updateSummary.setGrossSalary(gross);
        updateSummary.setDeductionTotal(deduction);
        updateSummary.setTaxTotal(tax);
        updateSummary.setNetSalary(net);
        updateSummary.setCalcStatus(1);

        // 重新封存 JSON
        SalarySnapshotDTO snapshot = new SalarySnapshotDTO();
        snapshot.setGrossSalary(gross);
        snapshot.setDeductionTotal(deduction);
        snapshot.setTaxTotal(tax);
        snapshot.setNetSalary(net);
        updateSummary.setDetailJson(JSON.toJSONString(snapshot));

        iSalarySummaryService.updateById(updateSummary);
    }

    @Override
    public SummaryVO previewCalculateByPeriod(Long periodId) {
        // 对于预览，最佳实践是开启一个只读事务或执行一套脱离落库的逻辑
        // 由于这属于非核心高频接口，目前返回空或复用核心逻辑屏蔽 save 操作。
        throw new UnsupportedOperationException("V3.0暂不提供非落盘预览，请使用单人重新核算替代");
    }

    // ==========================================
    // 5. 内部辅助方法 (Helpers)
    // ==========================================

    /**
     * 构建统一明细实体辅助方法
     */
    private SalaryItemDetail buildItemDetail(SalaryPeriod period, Long summaryId, SalaryItemConfig config, BigDecimal amount, Integer sourceType, String remark) {
        SalaryItemDetail detail = new SalaryItemDetail();
        detail.setEmployeeId(period.getEmployeeId());
        detail.setPeriodId(period.getId());
        detail.setSummaryId(summaryId);
        detail.setItemType(config.getItemCategory());
        detail.setItemConfigId(config.getId());
        detail.setItemCode(config.getItemCode());
        detail.setItemName(config.getItemName());
        detail.setCategoryDictValue(config.getCategoryDictValue());
        detail.setSourceType(sourceType);
        detail.setOriginalCurrency("CNY");
        detail.setOriginalAmount(amount);
        detail.setExchangeRate(BigDecimal.ONE);
        detail.setSettlementAmount(amount);
        detail.setSettlementCurrency("CNY");
        detail.setRemark(remark);
        return detail;
    }

    /**
     * 基于时间切片的档案寻址机 (Temporal Matching)
     */
    private SalaryArchiveVO matchArchiveByTimeSlice(Long employeeId, String settlementMonth) {
        LocalDate periodEndDate = DateUtil.endOfMonth(DateUtil.parse(settlementMonth, "yyyyMM")).toLocalDateTime().toLocalDate();

        SalaryArchive targetArchive = iSalaryArchiveService.getOne(
                Wrappers.<SalaryArchive>lambdaQuery()
                        .eq(SalaryArchive::getEmployeeId, employeeId)
                        .eq(SalaryArchive::getAuditStatus, 1)
                        .le(SalaryArchive::getEffectiveDate, periodEndDate)
                        .ge(SalaryArchive::getExpiryDate, periodEndDate)
                        .orderByDesc(SalaryArchive::getEffectiveDate)
                        .last("LIMIT 1")
        );

        if (targetArchive == null) return null;
        return iSalaryArchiveService.getArchiveDetail(targetArchive.getId());
    }
}