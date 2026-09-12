package com.salary.admin.service.impl.salary.engine;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.salary.admin.model.dto.engine.SalaryCalcSingleReqDTO;
import com.salary.admin.model.entity.salary.SalaryItemDetail;
import com.salary.admin.model.entity.salary.SalarySummary;
import com.salary.admin.service.salary.ISalaryItemDetailService;
import com.salary.admin.service.salary.ISalarySummaryService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 真实核算处理器：负责落库与事务控制
 */
@Service
public class SalaryPersistProcessor extends AbstractSalaryProcessor<Void> {

    @Resource
    private ISalaryItemDetailService detailService;
    @Resource
    private ISalarySummaryService summaryService;

    // 🌟 核心修复：重写方法并标注事务，确保被 Spring CGLIB 正常代理
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Void process(SalaryCalcSingleReqDTO reqDTO) {
        super.process(reqDTO);
        return null; // 真实落库不需要返回具体对象
    }

    @Override
    protected Void handleResult(SalaryCalcSingleReqDTO reqDTO, SalaryCalcAggregator aggregator, Map<String, Object> env) {
        Long periodId = reqDTO.getPeriodId();
        Long employeeId = reqDTO.getEmployeeId();

        // 多币种结算：明细币种 = 员工档案币种, 汇率取自当月汇率表 (回退 1)
        String currency = env.get("settlementCurrency") != null
                ? env.get("settlementCurrency").toString() : "CNY";
        BigDecimal exchangeRate = env.get("exchangeRate") instanceof BigDecimal
                ? (BigDecimal) env.get("exchangeRate") : BigDecimal.ONE;

        // 1. 将 DTO 领域对象映射为数据库 Entity，并注入来源标识
        List<SalaryItemDetail> details = aggregator.getAllItems().stream().map(dto -> {
            dto.setSource("SYSTEM_CALC");
            return new SalaryItemDetail()
                    .setPeriodId(periodId)
                    .setEmployeeId(employeeId)
                    .setSummaryId(reqDTO.getSummaryId())
                    .setItemConfigId(dto.getExtConfigId())
                    .setItemCode(dto.getItemCode())
                    .setItemName(dto.getItemName())
                    .setOriginalCurrency(currency)
                    .setOriginalAmount(dto.getSettlementAmount())
                    .setExchangeRate(exchangeRate)
                    .setSettlementCurrency(currency)
                    .setSettlementAmount(dto.getSettlementAmount())
                    .setItemType(dto.getExtItemType())
                    .setSourceType(2) // 2-引擎计算
                    .setCalcPriority(dto.getSort());
        }).collect(Collectors.toList());

        // 2. 幂等控制：删除历史明细
        detailService.remove(new LambdaQueryWrapper<SalaryItemDetail>()
                .eq(SalaryItemDetail::getPeriodId, periodId)
                .eq(SalaryItemDetail::getEmployeeId, employeeId));

        // 3. 批量持久化明细
        if (!details.isEmpty()) detailService.saveBatch(details);

        // 4. 更新主账套
        SalarySummary summary = summaryService.getSummaryByUnique(periodId, employeeId);
        if (summary != null) {
            summary.setIncomeTotal(aggregator.getIncomeTotal())
                    .setDeductionTotal(aggregator.getDeductionTotal())
                    .setTaxTotal(aggregator.getTaxTotal())
                    .setGrossSalary(aggregator.getIncomeTotal())
                    .setNetSalary(aggregator.getNetSalary())
                    .setCalcStatus(1) // 1-成功
                    // calc_version 由 @Version 乐观锁自动递增 (重算追溯)
                    .setDetailJson(JSONUtil.toJsonStr(aggregator.getSnapshot()));

            summaryService.updateById(summary);
        }
        return null;
    }
}