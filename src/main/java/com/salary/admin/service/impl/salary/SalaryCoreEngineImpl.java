package com.salary.admin.service.impl.salary;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.salary.admin.model.dto.salary.period.PeriodAddReqDTO;
import com.salary.admin.model.dto.salary.period.PeriodBatchInitReqDTO;
import com.salary.admin.model.entity.salary.SalaryPaymentRecord;
import com.salary.admin.model.entity.salary.SalaryPeriod;
import com.salary.admin.model.entity.salary.SalarySummary;
import com.salary.admin.model.vo.salary.archive.SalaryArchiveVO;
import com.salary.admin.service.salary.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 薪资核心引擎实现类
 * 充当薪资模块的“总调度室”，专门处理跨表、跨业务的复杂逻辑。
 * 解决原先单体 Service 之间因为业务互相调用导致的循环依赖问题。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SalaryCoreEngineImpl implements ISalaryCoreEngine {
    // 核心引擎只注入基础 Service，基础 Service 内部不再互相注入
    private final ISalaryPeriodService iSalaryPeriodService;
    private final ISalarySummaryService iSalarySummaryService;
    private final ISalaryPaymentRecordService iSalaryPaymentRecordService;
    private final ISalaryIncomeDetailService iSalaryIncomeDetailService;
    private final ISalaryDeductionDetailService iSalaryDeductionDetailService;

    private final ISalaryArchiveService iSalaryArchiveService;

    private final ISalaryEmployeeService iSalaryEmployeeService;

    // 接管批量初始化
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchInitPeriods(PeriodBatchInitReqDTO reqDTO) {
        // 1. 调用底层的纯净服务，拿到刚刚建好的周期列表
        List<SalaryPeriod> newPeriods = iSalaryPeriodService.batchInitPeriodsOnly(reqDTO);

        if (CollUtil.isNotEmpty(newPeriods)) {
            // 2. 拿着这个列表，去联动生成汇总表
            this.initSummaryForPeriods(newPeriods);
        }
        return true;
    }

    /**
     * 场景：批量初始化月份汇总
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initSummaryForPeriods(List<SalaryPeriod> periods) {
        if (CollectionUtils.isEmpty(periods)) {
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
        iSalarySummaryService.batchInsert(summaries);
        log.info("SalaryCoreEngine: 联动初始化汇总表成功，记录数: {}", summaries.size());
    }

    @Override
    public Long createRecordByCalculation(Long summaryId, SalaryArchiveVO archive) {
        // 1. 根据 archive 档案数据，进行复杂的薪资计算...
        BigDecimal incomeTotal = BigDecimal.ZERO; // 计算出的收入
        BigDecimal deductionTotal = BigDecimal.ZERO; // 计算出的扣款
        BigDecimal finalSalary = incomeTotal.subtract(deductionTotal);

        // 2. 构建明细快照记录
        SalaryPaymentRecord record = new SalaryPaymentRecord();
        record.setSummaryId(summaryId);
        record.setEmployeeId(archive.getEmployeeId());
        record.setArchiveId(archive.getId());
        record.setBaseSalary(archive.getBaseSalary());
        record.setIncomeTotal(incomeTotal);
        record.setDeductionTotal(deductionTotal);
        record.setFinalSalary(finalSalary);
        record.setIsManual(0); // 系统计算
        // 3. 保存快照
        iSalaryPaymentRecordService.save(record);
        // 4. 同步刷新关联的汇总单金额
        this.refreshSummaryAmountBySummaryId(summaryId);

        return record.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRecordByManual(Long summaryId, Long employeeId, BigDecimal finalAmount, String remark) {
        // 1. 构建手动录入的快照记录
        SalaryPaymentRecord record = new SalaryPaymentRecord();
        record.setSummaryId(summaryId);
        record.setEmployeeId(employeeId);
        record.setFinalSalary(finalAmount);
        record.setIsManual(1); // 手动录入
        record.setRemark(remark);
        iSalaryPaymentRecordService.save(record);
        // 2. 刷新汇总单金额
        this.refreshSummaryAmountBySummaryId(summaryId);

        return record.getId();
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeGlobalSettlement(String settlementMonth) {
        log.info("🚀 [薪资引擎] 开始执行 {} 月份全员核算任务", settlementMonth);

        // 1. 基于周期驱动：获取该月份所有的薪资周期
        List<SalaryPeriod> periods = iSalaryPeriodService.list(
                Wrappers.<SalaryPeriod>lambdaQuery().eq(SalaryPeriod::getSettlementMonth, settlementMonth)
        );

        if (periods == null || periods.isEmpty()) {
            log.warn("⚠️ {} 月份没有找到任何薪资周期，请先执行周期初始化", settlementMonth);
            return;
        }

        for (SalaryPeriod period : periods) {
            // 2. 获取对应的汇总单 ID
            SalarySummary summary = iSalarySummaryService.getOne(
                    Wrappers.<SalarySummary>lambdaQuery().eq(SalarySummary::getPeriodId, period.getId())
            );
            if (summary == null) {
                log.warn("周期ID {} 缺失汇总单，跳过", period.getId());
                continue;
            }

            // 3. 获取员工核算月对应的生效档案 (需调用档案服务)
            SalaryArchiveVO archive = iSalaryArchiveService.getCurrentArchive(period.getEmployeeId());
            if (archive == null) {
                log.warn("员工ID {} 缺失生效薪资档案，跳过核算", period.getEmployeeId());
                continue;
            }

            // 4. 【核心防御】清理旧账，保证幂等性。允许财务无脑多次点击“重新核算”
            iSalaryPaymentRecordService.remove(
                    Wrappers.<SalaryPaymentRecord>lambdaQuery().eq(SalaryPaymentRecord::getSummaryId, summary.getId())
            );

            // 5. 执行单人核算并生成记录 (调用 Engine 自身的计算方法)
            // 注意：内部会包含金额计算和 refreshSummaryAmountBySummaryId 动作
            this.createRecordByCalculation(summary.getId(), archive);
        }

        log.info("✅ [薪资引擎] {} 月份全员核算任务执行完毕，共处理 {} 条周期", settlementMonth, periods.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncSummaryAmountByPeriodId(Long periodId) {
        SalarySummary summary = iSalarySummaryService.getOne(
                Wrappers.<SalarySummary>lambdaQuery().eq(SalarySummary::getPeriodId, periodId)
        );
        if (summary != null) {
            this.refreshSummaryAmountBySummaryId(summary.getId());
        }
    }

    @Override
    public void refreshSummaryAmountBySummaryId(Long summaryId) {
// 1. 查询该汇总单下所有的 PaymentRecord 快照
        List<SalaryPaymentRecord> records = iSalaryPaymentRecordService.list(
                Wrappers.<SalaryPaymentRecord>lambdaQuery().eq(SalaryPaymentRecord::getSummaryId, summaryId)
        );

        // 2. 累加计算总金额
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalDeduction = BigDecimal.ZERO;
        BigDecimal totalFinal = BigDecimal.ZERO;

        for (SalaryPaymentRecord record : records) {
            totalIncome = totalIncome.add(record.getIncomeTotal() != null ? record.getIncomeTotal() : BigDecimal.ZERO);
            totalDeduction = totalDeduction.add(record.getDeductionTotal() != null ? record.getDeductionTotal() : BigDecimal.ZERO);
            totalFinal = totalFinal.add(record.getFinalSalary() != null ? record.getFinalSalary() : BigDecimal.ZERO);
        }

        // 3. 更新汇总单
        SalarySummary updateSummary = new SalarySummary();
        updateSummary.setId(summaryId);
        updateSummary.setSalarySubtotal(totalIncome);
        updateSummary.setSalaryDeductionTotal(totalDeduction);
        updateSummary.setSalaryTotal(totalFinal);

        iSalarySummaryService.updateById(updateSummary);
    }

    // 接管单条新增
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addPeriodAndSummary(PeriodAddReqDTO reqDTO) {
        // 1. 调用底层的 PeriodService 只做周期表的保存
        Long periodId = iSalaryPeriodService.addPeriod(reqDTO);

        // 2. 联动生成汇总表
        SalaryPeriod period = iSalaryPeriodService.getById(periodId);
        this.initSummaryForPeriods(Collections.singletonList(period));

        return periodId;
    }
}