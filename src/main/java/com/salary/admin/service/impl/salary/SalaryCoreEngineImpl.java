package com.salary.admin.service.impl.salary;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.salary.admin.engine.SalaryRuleEngine;
import com.salary.admin.model.dto.salary.snapshot.SalaryDetailItemDTO;
import com.salary.admin.model.dto.salary.snapshot.SalarySnapshotDTO;
import com.salary.admin.model.entity.salary.*;
import com.salary.admin.model.vo.salary.archive.SalaryArchiveVO;
import com.salary.admin.service.salary.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 薪资核心引擎实现类
 * <p>
 * 核心定位：
 * - 作为薪资模块的“总调度室”，负责跨表、跨业务的复杂逻辑编排。
 * - 避免 Service 之间循环依赖，所有跨表逻辑集中在这里。
 * - 引擎层只做编排，不直接写表，底层操作交给基础 Service。
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
    private final ISalaryCalcPipelineService iSalaryCalcPipelineService;
    private final ISalaryCalcRuleService iSalaryCalcRuleService;
    private final ISalaryCalcContextService iSalaryCalcContextService;
    private final ISalaryCalcLogService iSalaryCalcLogService;
    private final ISalaryItemDetailService iSalaryItemDetailService;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRecordByCalculation(Long summaryId, SalaryArchiveVO archive, SalaryPeriod period) {
        long engineStartTime = System.currentTimeMillis();

        // ==========================================================
        // 1. 幂等性清理：清空该周期下所有旧的明细、日志、上下文
        // ==========================================
        iSalaryItemDetailService.remove(Wrappers.<SalaryItemDetail>lambdaQuery().eq(SalaryItemDetail::getSummaryId, summaryId));
        iSalaryCalcLogService.remove(Wrappers.<SalaryCalcLog>lambdaQuery().eq(SalaryCalcLog::getPeriodId, period.getId()));
        iSalaryCalcContextService.remove(Wrappers.<SalaryCalcContext>lambdaQuery().eq(SalaryCalcContext::getPeriodId, period.getId()));

        // ==========================================================
        // 2. 初始化全局引擎上下文 (Context)
        // ==========================================
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("baseSalary", archive.getBaseSalary() != null ? archive.getBaseSalary() : BigDecimal.ZERO);
        ctx.put("monthDays", period.getMonthDays() != null ? period.getMonthDays() : new BigDecimal("21.75"));
        ctx.put("attendanceDays", period.getAttendanceDays() != null ? period.getAttendanceDays() : BigDecimal.ZERO);
        ctx.put("taxRuleCode", archive.getTaxRuleCode()); // 关联计算规则

        // 获取全局发薪项元数据字典 (ItemConfig)
        Map<String, SalaryItemConfig> itemConfigMap = iSalaryItemConfigService.list().stream()
                .collect(Collectors.toMap(SalaryItemConfig::getItemCode, c -> c));

        List<SalaryItemDetail> allDetails = new ArrayList<>(); // 统一存放所有生成的明细

        // ==========================================================
        // 3. 预处理：加载档案固定项 (Source = 1) 并推入上下文
        // ==========================================
        List<SalaryArchiveItem> archiveItems = iSalaryArchiveItemService.list(
                Wrappers.<SalaryArchiveItem>lambdaQuery().eq(SalaryArchiveItem::getArchiveId, archive.getId())
        );
        for (SalaryArchiveItem aItem : archiveItems) {
            SalaryItemConfig config = iSalaryItemConfigService.getById(aItem.getItemConfigId());
            if (config != null) {
                BigDecimal amount = aItem.getAmount() != null ? aItem.getAmount() : BigDecimal.ZERO;
                // 写入上下文，供 Pipeline 读取
                ctx.put(config.getEnvVarName(), amount);

                // 生成统一明细记录 (SourceType = 1-薪资档案)
                SalaryItemDetail detail = buildItemDetail(period, summaryId, config, amount, 1, "从员工档案直接引入");
                detail.setArchiveId(archive.getId());
                detail.setArchiveItemId(aItem.getId());
                allDetails.add(detail);
            }
        }

        // ==========================================================
        // 4. 加载执行管道与规则库
        // ==========================================
        // 此处可通过参数或员工属性动态决定使用哪条 Pipeline，默认使用 DEFAULT
        List<SalaryCalcPipeline> pipelines = iSalaryCalcPipelineService.list(
                Wrappers.<SalaryCalcPipeline>lambdaQuery()
                        .eq(SalaryCalcPipeline::getPipelineCode, "DEFAULT_PIPELINE")
                        .eq(SalaryCalcPipeline::getStatus, 1)
                        .orderByAsc(SalaryCalcPipeline::getStage, SalaryCalcPipeline::getSortOrder)
        );

        Map<String, SalaryCalcRule> ruleMap = iSalaryCalcRuleService.list(
                Wrappers.<SalaryCalcRule>lambdaQuery().eq(SalaryCalcRule::getStatus, 1)
        ).stream().collect(Collectors.toMap(SalaryCalcRule::getRuleCode, r -> r));

        List<SalaryCalcLog> calcLogs = new ArrayList<>();

        // ==========================================================
        // 5. 🚀 引擎轰鸣：严格按照 Pipeline Stage 顺次执行
        // ==========================================
        for (SalaryCalcPipeline step : pipelines) {
            long stepStartTime = System.currentTimeMillis();
            SalaryCalcRule rule = ruleMap.get(step.getRuleCode());
            if (rule == null) continue;

            BigDecimal resultValue = BigDecimal.ZERO;
            String errorMsg = null;

            try {
                // 5.1 执行 AviatorScript 表达式
                resultValue = salaryRuleEngine.execute(rule.getRuleScript(), ctx);

                // 5.2 瀑布流反写：将计算结果放回上下文，供下游规则使用！
                ctx.put(rule.getRuleCode(), resultValue);

                // 5.3 核心判别：如果该 RuleCode 是一个标准的“薪资发薪项”（在 config 表有定义），且金额不为 0
                // 则它需要体现在工资条上，生成 ItemDetail；否则，它只是个中间变量（如：应纳税所得额）
                SalaryItemConfig config = itemConfigMap.get(rule.getRuleCode());
                if (config != null && resultValue.compareTo(BigDecimal.ZERO) != 0) {

                    SalaryItemDetail detail = buildItemDetail(period, summaryId, config, resultValue, 2, "引擎计算生成");
                    detail.setRuleCode(rule.getRuleCode());
                    detail.setCalcSnapshot(JSON.toJSONString(ctx)); // 保留当时计算时的所有变量快照
                    detail.setCalcPriority(step.getSortOrder());

                    allDetails.add(detail);
                }

            } catch (Exception e) {
                errorMsg = e.getMessage();
                log.error("Pipeline 规则执行异常! Code: {}, 脚本: {}", rule.getRuleCode(), rule.getRuleScript(), e);
                throw new RuntimeException("薪资核算中断于规则 [" + rule.getRuleName() + "]: " + e.getMessage());
            } finally {
                // 5.4 无论成功失败，必须记录执行日志
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

        // ==========================================================
        // 6. 财务汇总与数据归集
        // ==========================================
        BigDecimal grossSalary = BigDecimal.ZERO;
        BigDecimal deductionTotal = BigDecimal.ZERO;
        BigDecimal taxTotal = BigDecimal.ZERO;

        List<SalaryDetailItemDTO> snapshotItems = new ArrayList<>();

        // 遍历所有明细（含档案带来的和引擎算出的），按分类累加大盘资金
        for (SalaryItemDetail d : allDetails) {
            if (d.getItemType() == 1) {
                grossSalary = grossSalary.add(d.getSettlementAmount());
            } else if (d.getItemType() == 2) {
                deductionTotal = deductionTotal.add(d.getSettlementAmount());
            } else if (d.getItemType() == 3) {
                taxTotal = taxTotal.add(d.getSettlementAmount());
            }

            // 映射到供前端展示的 JSON DTO 中
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

        // ==========================================================
        // 7. 持久化落盘 (DB 写入)
        // ==========================================

        // 7.1 保存上下文与日志审计
        SalaryCalcContext contextEntity = new SalaryCalcContext();
        contextEntity.setEmployeeId(period.getEmployeeId());
        contextEntity.setPeriodId(period.getId());
        contextEntity.setPipelineCode("DEFAULT_PIPELINE");
        contextEntity.setContextJson(JSON.toJSONString(ctx));
        iSalaryCalcContextService.save(contextEntity);

        if (CollUtil.isNotEmpty(calcLogs)) iSalaryCalcLogService.saveBatch(calcLogs);

        // 7.2 批量保存统一明细账本
        if (CollUtil.isNotEmpty(allDetails)) iSalaryItemDetailService.saveBatch(allDetails);

        // 7.3 组装工资单 JSON 快照并更新大盘 (Summary)
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

        // 优雅切分渲染区域
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
        //既然实体类要 String，我们就用 Fastjson2 把对象转成 String
        updateSummary.setDetailJson(JSON.toJSONString(snapshot));

        iSalarySummaryService.updateById(updateSummary);

        log.info("🎯 管道计算执行完毕 [员工ID:{}]. 应发:{}, 实发:{}, 耗时:{}ms",
                period.getEmployeeId(), grossSalary, netSalary, (System.currentTimeMillis() - engineStartTime));

        return summaryId;
    }

    /**
     * 辅助方法：构建统一明细实体
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
}
