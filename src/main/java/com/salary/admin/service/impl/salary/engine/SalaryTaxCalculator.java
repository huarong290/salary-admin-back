package com.salary.admin.service.impl.salary.engine;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.hutool.json.JSONUtil;
import com.salary.admin.model.entity.salary.SalarySummary;
import com.salary.admin.service.salary.ISalarySummaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 个人所得税计算器 (居民个人综合所得 · 累计预扣预缴法)
 * <p>
 * 依据《个人所得税法》累计预扣法：
 * 本期应预扣预缴税额 = (累计预扣预缴应纳税所得额 × 预扣率 - 速算扣除数) - 累计减免税额 - 累计已预扣预缴税额
 * 累计预扣预缴应纳税所得额 = 累计收入 - 累计免税收入 - 累计减除费用(5000×月数) - 累计专项扣除 - 累计专项附加扣除
 * <p>
 * 累计收入口径：本月取引擎上下文注入的"应税收入"(_taxableIncome)，历史月份取汇总单快照
 * detail_json 中的 taxableIncomeTotal (老数据回退 gross_salary)，
 * 避免把不计税的免税收入计入累计税基而导致免税月份被补算出个税。
 * 累计减除费用按 5000×月数，专项扣除/附加扣除暂以全局配置注入为 0 (可通过 salary_config 扩展)。
 *
 * @author system
 * @since 2026-04-01
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SalaryTaxCalculator {

    private final ISalarySummaryService summaryService;

    /** 每月基本减除费用 (起征点) */
    private static final BigDecimal MONTHLY_BASIC_DEDUCTION = new BigDecimal("5000");

    /**
     * 七级超额累进税率表: {下限, 上限, 预扣率, 速算扣除数}
     */
    private static final BigDecimal[][] TAX_BRACKETS = {
            {new BigDecimal("0"), new BigDecimal("36000"), new BigDecimal("0.03"), new BigDecimal("0")},
            {new BigDecimal("36000"), new BigDecimal("144000"), new BigDecimal("0.10"), new BigDecimal("2520")},
            {new BigDecimal("144000"), new BigDecimal("300000"), new BigDecimal("0.20"), new BigDecimal("16920")},
            {new BigDecimal("300000"), new BigDecimal("420000"), new BigDecimal("0.25"), new BigDecimal("31920")},
            {new BigDecimal("420000"), new BigDecimal("660000"), new BigDecimal("0.30"), new BigDecimal("52920")},
            {new BigDecimal("660000"), new BigDecimal("960000"), new BigDecimal("0.35"), new BigDecimal("85920")},
            {new BigDecimal("960000"), null, new BigDecimal("0.45"), new BigDecimal("181920")}
    };

    /**
     * 计算指定员工在当前结算月份的个税 (累计预扣预缴法)
     *
     * @param employeeId          员工ID
     * @param settlementMonth     当前结算月份 (YYYYMM)
     * @param currentMonthGross   本次核算的本月应发收入 (未落盘时从引擎上下文注入)
     * @return 本月应预扣预缴税额 (>= 0, 精确到分)
     */
    public BigDecimal calculate(Long employeeId, String settlementMonth, BigDecimal currentMonthGross) {
        if (employeeId == null || settlementMonth == null || settlementMonth.isBlank()) {
            return BigDecimal.ZERO;
        }
        BigDecimal currentGross = currentMonthGross != null ? currentMonthGross : BigDecimal.ZERO;

        // 1. 查询该员工历史 (严格早于本月) 已核算结算单, 用于累计已缴与累计收入
        List<SalarySummary> summaries = summaryService.lambdaQuery()
                .eq(SalarySummary::getEmployeeId, employeeId)
                .lt(SalarySummary::getSettlementMonth, settlementMonth)
                .eq(SalarySummary::getCalcStatus, 1)
                .orderByAsc(SalarySummary::getSettlementMonth)
                .list();

        // 2. 累计口径: 历史月份收入 + 本月应发
        BigDecimal cumulativeIncome = currentGross;
        BigDecimal cumulativeTaxPaid = BigDecimal.ZERO;
        int monthCount = 1; // 本月占一个减除费用月

        for (SalarySummary s : summaries) {
            // 历史月份同样只能累计"应税收入"：否则不计税收入(如餐补)会进入累计税基，
            // 使免税月份在后续月份被补算出个税。优先取快照中的 taxableIncomeTotal，老数据回退应发合计。
            BigDecimal taxableOfMonth = resolveHistoricalTaxableIncome(s);
            BigDecimal tax = s.getTaxTotal() != null ? s.getTaxTotal() : BigDecimal.ZERO;
            cumulativeIncome = cumulativeIncome.add(taxableOfMonth);
            cumulativeTaxPaid = cumulativeTaxPaid.add(tax);
            monthCount++;
        }

        // 3. 累计减除费用 = 5000 × 月数
        BigDecimal cumulativeBasicDeduction = MONTHLY_BASIC_DEDUCTION.multiply(BigDecimal.valueOf(monthCount));

        // 4. 累计预扣预缴应纳税所得额 (下限截断为 0)
        BigDecimal taxableIncome = cumulativeIncome.subtract(cumulativeBasicDeduction);
        if (taxableIncome.compareTo(BigDecimal.ZERO) < 0) {
            taxableIncome = BigDecimal.ZERO;
        }

        // 5. 查税率表计算累计应纳税额
        BigDecimal cumulativeTaxDue = computeCumulativeTax(taxableIncome);

        // 6. 本期应预扣 = 累计应纳税额 - 累计已预缴 (负数归零, 留抵不退)
        BigDecimal currentTax = cumulativeTaxDue.subtract(cumulativeTaxPaid);
        if (currentTax.compareTo(BigDecimal.ZERO) < 0) {
            currentTax = BigDecimal.ZERO;
        }

        log.debug("个税累计预扣: 员工={}, 月份={}, 本月应发={}, 累计收入={}, 累计减除={}, 应纳税所得={}, 累计税额={}, 已预缴={}, 本月={}",
                employeeId, settlementMonth, currentGross, cumulativeIncome, cumulativeBasicDeduction,
                taxableIncome, cumulativeTaxDue, cumulativeTaxPaid, currentTax);

        return currentTax.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 取历史月份的"应税收入"(个税基数)
     * <p>
     * 优先从汇总单快照 detail_json 中读取引擎落库的 taxableIncomeTotal；
     * 老数据(快照中无该字段)回退使用应发合计，保持兼容。
     */
    private BigDecimal resolveHistoricalTaxableIncome(SalarySummary summary) {
        if (StringUtils.isNotBlank(summary.getDetailJson())) {
            try {
                BigDecimal taxable = JSONUtil.parseObj(summary.getDetailJson()).getBigDecimal("taxableIncomeTotal");
                if (taxable != null) {
                    return taxable;
                }
            } catch (Exception e) {
                log.warn("解析历史薪资快照失败, summaryId={}, 回退使用应发合计", summary.getId(), e);
            }
        }
        return summary.getGrossSalary() != null ? summary.getGrossSalary() : BigDecimal.ZERO;
    }

    /**
     * 根据累计应纳税所得额计算累计应纳税额
     */
    private BigDecimal computeCumulativeTax(BigDecimal taxableIncome) {
        for (BigDecimal[] bracket : TAX_BRACKETS) {
            BigDecimal lower = bracket[0];
            BigDecimal upper = bracket[1];
            BigDecimal rate = bracket[2];
            BigDecimal quickDeduction = bracket[3];
            if (upper == null) {
                // 最高档
                return taxableIncome.multiply(rate).subtract(quickDeduction);
            }
            if (taxableIncome.compareTo(lower) > 0 && taxableIncome.compareTo(upper) <= 0) {
                return taxableIncome.multiply(rate).subtract(quickDeduction);
            }
            if (taxableIncome.compareTo(lower) == 0) {
                return taxableIncome.multiply(rate).subtract(quickDeduction);
            }
        }
        return BigDecimal.ZERO;
    }
}
