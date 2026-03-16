package com.salary.admin.service.impl.salary;


import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.salary.admin.model.dto.salary.period.PeriodAddReqDTO;
import com.salary.admin.model.dto.salary.period.PeriodBatchInitReqDTO;
import com.salary.admin.model.entity.salary.*;
import com.salary.admin.model.vo.salary.archive.SalaryArchiveVO;
import com.salary.admin.service.salary.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    private final ISalaryArchiveItemService iSalaryArchiveItemService;

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
    public Long createRecordByCalculation(Long summaryId, SalaryArchiveVO archive, SalaryPeriod period) {
        // ==========================================
        // 1. 基础工资与出勤折算
        // ==========================================
        BigDecimal baseSalary = archive.getBaseSalary() != null ? archive.getBaseSalary() : BigDecimal.ZERO;
        BigDecimal monthDays = (period.getMonthDays() != null && period.getMonthDays() > 0)
                ? new BigDecimal(period.getMonthDays())
                : new BigDecimal("21.75"); // 默认法定计薪天数
        BigDecimal attendanceDays = period.getAttendanceDays() != null
                ? new BigDecimal(period.getAttendanceDays())
                : BigDecimal.ZERO;

        // 实发底薪 = (底薪 / 计薪天数) * 出勤天数
        BigDecimal proratedBaseSalary = baseSalary.divide(monthDays, 4, RoundingMode.HALF_UP)
                .multiply(attendanceDays)
                .setScale(2, RoundingMode.HALF_UP);

        // ==========================================
        // 2. 初始化统计变量与快照 JSON 对象
        // ==========================================
        BigDecimal incomeTotal = proratedBaseSalary; // 收入先计入折算后的底薪
        BigDecimal deductionTotal = BigDecimal.ZERO;

        // 用于存储明细快照，方便前端查阅和后期审计
        JSONObject detailJson = new JSONObject();
        detailJson.put("baseSalary", baseSalary);
        detailJson.put("monthDays", monthDays);
        detailJson.put("attendanceDays", attendanceDays);
        detailJson.put("proratedBaseSalary", proratedBaseSalary);

        // ==========================================
        // 3. 处理薪资档案中的【固定配置项】 (津贴、社保等)
        // ==========================================
        List<SalaryArchiveItem> archiveItems = iSalaryArchiveItemService.list(
                Wrappers.<SalaryArchiveItem>lambdaQuery().eq(SalaryArchiveItem::getArchiveId, archive.getId())
        );

        BigDecimal archiveIncome = BigDecimal.ZERO;
        BigDecimal archiveDeduction = BigDecimal.ZERO;

        for (SalaryArchiveItem item : archiveItems) {
            BigDecimal itemAmount = BigDecimal.ZERO;

            // 判定计算方式：1-固定金额, 2-按基数比例
            if (item.getCalcType() == 1) {
                itemAmount = item.getAmount() != null ? item.getAmount() : BigDecimal.ZERO;
            } else if (item.getCalcType() == 2) {
                // 取基数（为空则取主表底薪）
                BigDecimal calcBase = (item.getBaseAmount() != null && item.getBaseAmount().compareTo(BigDecimal.ZERO) > 0)
                        ? item.getBaseAmount()
                        : baseSalary;
                BigDecimal ratio = item.getRatio() != null ? item.getRatio() : BigDecimal.ZERO;
                itemAmount = calcBase.multiply(ratio).setScale(2, RoundingMode.HALF_UP);
            }

            // 判定加减项：1-收入项, 2-扣款项
            if (item.getItemType() == 1) {
                archiveIncome = archiveIncome.add(itemAmount);
            } else if (item.getItemType() == 2) {
                archiveDeduction = archiveDeduction.add(itemAmount);
            }
            // 将每一项的计算结果也存入快照 (使用 typeId 作为 key)
            detailJson.put("archive_item_" + item.getTypeId(), itemAmount);
        }

        incomeTotal = incomeTotal.add(archiveIncome);
        deductionTotal = deductionTotal.add(archiveDeduction);

        // ==========================================
        // 4. 处理当月【临时变动项】 (奖金、罚款、请假扣除等)
        // ==========================================
        // 查询当月临时收入
        List<SalaryIncomeDetail> periodIncomes = iSalaryIncomeDetailService.list(
                Wrappers.<SalaryIncomeDetail>lambdaQuery().eq(SalaryIncomeDetail::getPeriodId, period.getId())
        );
        BigDecimal periodIncomeTotal = periodIncomes.stream()
                .map(SalaryIncomeDetail::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 查询当月临时扣款
        List<SalaryDeductionDetail> periodDeductions = iSalaryDeductionDetailService.list(
                Wrappers.<SalaryDeductionDetail>lambdaQuery().eq(SalaryDeductionDetail::getPeriodId, period.getId())
        );
        BigDecimal periodDeductionTotal = periodDeductions.stream()
                .map(SalaryDeductionDetail::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        incomeTotal = incomeTotal.add(periodIncomeTotal);
        deductionTotal = deductionTotal.add(periodDeductionTotal);

        detailJson.put("periodIncomeTotal", periodIncomeTotal);
        detailJson.put("periodDeductionTotal", periodDeductionTotal);

        // ==========================================
        // 5. 最终实发计算与数据入库
        // ==========================================
        BigDecimal finalSalary = incomeTotal.subtract(deductionTotal);

        // 构建明细快照记录
        SalaryPaymentRecord record = new SalaryPaymentRecord();
        record.setSummaryId(summaryId);
        record.setEmployeeId(archive.getEmployeeId());
        record.setArchiveId(archive.getId());

        // 核心金额赋值
        record.setBaseSalary(baseSalary);
        record.setIncomeTotal(incomeTotal);
        record.setDeductionTotal(deductionTotal);
        record.setFinalSalary(finalSalary);

        record.setIsManual(0); // 系统自动计算
        record.setDetailJson(detailJson.toString()); // 🌟 存入完美的防篡改计算快照

        // 保存记录并刷新汇总表
        iSalaryPaymentRecordService.save(record);
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

            // 5. 执行单人核算并生成记录 (调用 Engine 自身的计算方法把 period 也传进去)
            // 注意：内部会包含金额计算和 refreshSummaryAmountBySummaryId 动作
            this.createRecordByCalculation(summary.getId(), archive, period);
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