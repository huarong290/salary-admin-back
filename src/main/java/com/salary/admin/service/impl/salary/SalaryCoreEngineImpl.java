package com.salary.admin.service.impl.salary;


import com.salary.admin.mapper.ext.salary.SalarySummaryExtMapper;
import com.salary.admin.model.entity.salary.SalaryPeriod;
import com.salary.admin.model.entity.salary.SalarySummary;
import com.salary.admin.service.salary.ISalaryCoreEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 薪资核心引擎实现类
 */
@Slf4j
@Service
public class SalaryCoreEngineImpl implements ISalaryCoreEngine {

    @Autowired
    private SalarySummaryExtMapper salarySummaryExtMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initSummaryForPeriods(List<SalaryPeriod> periods) {
        if (periods == null || periods.isEmpty()) {
            return;
        }

        // 构造汇总数据
        List<SalarySummary> summaries = periods.stream().map(p -> {
            SalarySummary s = new SalarySummary();
            s.setPeriodId(p.getId());
            s.setCurrency("CNY");
            s.setExchangeRate(BigDecimal.ONE);
            s.setSalarySubtotal(BigDecimal.ZERO);      // 初始应发 0
            s.setSalaryDeductionTotal(BigDecimal.ZERO); // 初始扣款 0
            s.setSalaryTotal(BigDecimal.ZERO);          // 初始实发 0
            s.setPaymentStatus(0);                      // 未支付
            return s;
        }).collect(Collectors.toList());

        // 2. 🌟 执行批量保存
        // 如果你的 Mapper 继承了 BaseMapper，直接循环插入或使用自定义批量方法
        // 注意：Mapper 接口本身没有 saveBatch，这里我们循环插入，或者调用你 ExtMapper 里定义的 batchInsert
        salarySummaryExtMapper.batchInsert(summaries);
        log.info("SalaryCoreEngine: 联动初始化汇总表成功，记录数: {}", summaries.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncSummaryAmount(Long periodId) {
        // TODO: 后续在此处实现【一键计算】逻辑
        // 1. 调用 IncomeDetailService 汇总金额
        // 2. 调用 DeductionDetailService 汇总金额
        // 3. 更新 SalarySummary 记录
        log.info("SalaryCoreEngine: 预留计算接口，正在处理周期ID: {}", periodId);
    }
}