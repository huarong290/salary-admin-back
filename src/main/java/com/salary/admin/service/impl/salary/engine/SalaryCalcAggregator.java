package com.salary.admin.service.impl.salary.engine;

import com.salary.admin.model.dto.salary.snapshot.SalaryDetailItemDTO;
import com.salary.admin.model.dto.salary.snapshot.SalarySnapshotDTO;
import com.salary.admin.model.entity.salary.SalaryCalcPipelineStep;
import com.salary.admin.model.entity.salary.SalaryItemConfig;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 薪资计算上下文聚合器 (充血模型)
 * 职责：专注纯内存的数据累加与快照构建，隔离持久化逻辑
 */
@Getter
public class SalaryCalcAggregator {
    // 累加器状态
    private BigDecimal incomeTotal = BigDecimal.ZERO;
    private BigDecimal deductionTotal = BigDecimal.ZERO;
    private BigDecimal taxTotal = BigDecimal.ZERO;
    private BigDecimal companyExpenseTotal = BigDecimal.ZERO;
    /** 应税收入合计: 仅累加 taxable_flag=1 的收入项 (个税基数) */
    private BigDecimal taxableIncomeTotal = BigDecimal.ZERO;

    // 业务快照数据载体
    private final SalarySnapshotDTO snapshot = new SalarySnapshotDTO();

    public SalaryCalcAggregator() {
        snapshot.setIncome(new ArrayList<>());
        snapshot.setDeduction(new ArrayList<>());
        snapshot.setTax(new ArrayList<>());
        snapshot.setCompanyExpense(new ArrayList<>());
    }

    /**
     * 核心累加逻辑：收拢所有防御性计算和正负转换规则
     *
     * @param step           管道步骤
     * @param originalAmount 计算结果金额
     * @param config         全局薪资项目配置 (可为 null)
     * @param taxableOverride 档案级计税覆盖 (非 null 时优先于全局配置, 仅对收入类生效)
     */
    public void accumulate(SalaryCalcPipelineStep step, BigDecimal originalAmount, SalaryItemConfig config, Integer taxableOverride) {
        int itemType = config != null ? config.getItemCategory() : 1;

        // 1. 引入新变量，保证入参 immutable
        BigDecimal calcAmount = originalAmount;

        // 2. 动态路由拦截：收入类且为负数，强制“叛变”为扣款类
        if (itemType == 1 && calcAmount.compareTo(BigDecimal.ZERO) < 0) {
            itemType = 2;
            calcAmount = calcAmount.abs();
        }

        // 3. 规范展示金额：除收入类外，其余强制取绝对值
        BigDecimal displayAmount = (itemType == 1) ? calcAmount : calcAmount.abs();

        // 4. 构建纯洁的 DTO 快照节点 (不绑定特定的 source，由下游处理器决定)
        SalaryDetailItemDTO snapshotItem = SalaryDetailItemDTO.builder()
                .itemCode(step.getRuleCode())
                .itemName(step.getRuleName())
                .settlementAmount(displayAmount)
                .calcLog("公式规则: " + step.getRuleCode())
                .sort(step.getSortOrder())
                // 巧妙利用扩展字段暂存元数据，供后续 Persist 处理器映射 Entity 使用
                .extConfigId(config != null ? config.getId() : 0L)
                .extItemType(itemType)
                .build();

        // 5. 状态分发与金额累加
        switch (itemType) {
            case 1 -> {
                snapshot.getIncome().add(snapshotItem);
                incomeTotal = incomeTotal.add(calcAmount);
                // 计税口径(仅收入类): 档案覆盖值 > 全局配置 taxable_flag > 默认计税(防漏税)
                Integer effectiveTaxable = taxableOverride != null
                        ? taxableOverride
                        : (config != null ? config.getTaxableFlag() : null);
                if (effectiveTaxable == null || effectiveTaxable == 1) {
                    taxableIncomeTotal = taxableIncomeTotal.add(calcAmount);
                }
            }
            case 2 -> {
                snapshot.getDeduction().add(snapshotItem);
                deductionTotal = deductionTotal.add(calcAmount.abs()); // 防御负负得正
            }
            case 3 -> {
                snapshot.getTax().add(snapshotItem);
                taxTotal = taxTotal.add(calcAmount.abs());
            }
            case 4 -> {
                snapshot.getCompanyExpense().add(snapshotItem);
                companyExpenseTotal = companyExpenseTotal.add(calcAmount.abs());
            }
        }
    }

    /**
     * 计算实发薪资 (收入 - 扣款 - 税费)
     */
    public BigDecimal getNetSalary() {
        return incomeTotal.subtract(deductionTotal).subtract(taxTotal);
    }

    /**
     * 扁平化获取所有快照明细，供落库或展示遍历使用
     */
    public List<SalaryDetailItemDTO> getAllItems() {
        List<SalaryDetailItemDTO> all = new ArrayList<>();
        all.addAll(snapshot.getIncome());
        all.addAll(snapshot.getDeduction());
        all.addAll(snapshot.getTax());
        all.addAll(snapshot.getCompanyExpense());
        return all;
    }

    /**
     * 关账：补全全局快照的汇总数据和环境变量
     */
    public void finalizeSnapshot(Map<String, Object> env, String pipelineCode) {
        snapshot.setGrossSalary(incomeTotal);
        snapshot.setDeductionTotal(deductionTotal);
        snapshot.setTaxTotal(taxTotal);
        snapshot.setNetSalary(getNetSalary());
        // 计税基数：仅 taxable_flag=1 的收入项 (个税口径)
        snapshot.setTaxableIncomeTotal(taxableIncomeTotal);
        // 多币种结算：币种与汇率取自员工档案上下文 (未注入回退 CNY / 1)
        snapshot.setSettlementCurrency(env.getOrDefault("settlementCurrency", "CNY").toString());
        snapshot.setExchangeRate(env.get("exchangeRate") instanceof Number
                ? (BigDecimal) env.get("exchangeRate") : BigDecimal.ONE);
        snapshot.setCalcRemark("引擎计算成功，使用的管道: " + pipelineCode);

        // 提取基础环境变量存档
        snapshot.setAttendanceDays((BigDecimal) env.getOrDefault("attendanceDays", BigDecimal.ZERO));
        snapshot.setMonthDays((BigDecimal) env.getOrDefault("monthDays", BigDecimal.ZERO));
        snapshot.setBaseSalary((BigDecimal) env.getOrDefault("baseSalary", BigDecimal.ZERO));
    }
}
