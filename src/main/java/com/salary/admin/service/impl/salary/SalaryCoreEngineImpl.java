package com.salary.admin.service.impl.salary;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.salary.admin.model.dto.salary.period.PeriodAddReqDTO;
import com.salary.admin.model.dto.salary.period.PeriodBatchInitReqDTO;
import com.salary.admin.model.dto.salary.snapshot.SalaryDetailItemDTO;
import com.salary.admin.model.dto.salary.snapshot.SalarySnapshotDTO;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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


    // 注入字典表服务（用于给 FIXED 项目反查名称和分类）
    private final ISalaryIncomeTypeService iSalaryIncomeTypeService;

    private final ISalaryDeductionTypeService iSalaryDeductionTypeService;

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
        // 1. 基础工资与出勤严谨折算
        // ==========================================
        BigDecimal baseSalary = archive.getBaseSalary() != null ? archive.getBaseSalary() : BigDecimal.ZERO;

        //使用 compareTo 比较大小，且直接引用 BigDecimal 对象
        BigDecimal monthDays = (period.getMonthDays() != null && period.getMonthDays().compareTo(BigDecimal.ZERO) > 0)
                ? period.getMonthDays()
                : new BigDecimal("21.75"); // 默认法定计薪天数

        // 直接引用，消除多余的 new BigDecimal()
        BigDecimal attendanceDays = period.getAttendanceDays() != null
                ? period.getAttendanceDays()
                : BigDecimal.ZERO;

        BigDecimal proratedBaseSalary = baseSalary.divide(monthDays, 4, RoundingMode.HALF_UP)
                .multiply(attendanceDays).setScale(2, RoundingMode.HALF_UP);

        BigDecimal incomeTotal = BigDecimal.ZERO;
        BigDecimal deductionTotal = BigDecimal.ZERO;

        // 🌟 使用你定义的完美 DTO 集合来代替 JSONObject
        List<SalaryDetailItemDTO> snapshotItems = new ArrayList<>();

        // --- 写入底薪明细 ---
        incomeTotal = incomeTotal.add(proratedBaseSalary);
        snapshotItems.add(SalaryDetailItemDTO.builder()
                .itemName("基本工资")
                .amount(proratedBaseSalary)
                .itemType(1)
                .category("基本薪酬")
                .source("BASE")
                .formula(String.format("底薪 %s ÷ %s天 × 出勤 %s天", baseSalary, monthDays, attendanceDays))
                .build());
        // ==========================================
        // 🌟 1.5 核心注入：全勤奖自动核算 (零信任机制)
        // ==========================================
        // 前提：请确保你的 SalaryArchiveVO 里已经加上了 fullAttendanceBonus 字段
        BigDecimal fullAttendanceBonus = archive.getFullAttendanceBonus() != null
                ? archive.getFullAttendanceBonus() : BigDecimal.ZERO;
// 只有当员工档案里配置了全勤奖标准（>0）时，系统才去判定
        if (fullAttendanceBonus.compareTo(BigDecimal.ZERO) > 0) {
            // 引擎只认底层周期表的裁决开关
            if (Integer.valueOf(1).equals(period.getFullAttendanceFlag())) {
                // 开关为 1 (满勤)：痛快发钱！
                incomeTotal = incomeTotal.add(fullAttendanceBonus);
                snapshotItems.add(SalaryDetailItemDTO.builder()
                        .itemName("全勤奖")
                        .amount(fullAttendanceBonus)
                        .itemType(1) // 1: 收入
                        .category("奖金福利")
                        .source("SYSTEM_CALC") // 系统自动计算判定
                        .formula("考勤系统/HR裁定为[满勤]，按档案标准全额发放")
                        .build());
            } else {
                // 开关为 0 (非满勤)：一分不给，且白纸黑字写入快照，杜绝月末扯皮！
                snapshotItems.add(SalaryDetailItemDTO.builder()
                        .itemName("全勤奖")
                        .amount(BigDecimal.ZERO)
                        .itemType(1)
                        .category("奖金福利")
                        .source("SYSTEM_CALC")
                        .formula("考勤系统/HR裁定为[非满勤]，未达标不予发放")
                        .build());
            }
        }
        // ==========================================
        // 2. 提前拉取字典，用于翻译 FIXED 档案项
        // ==========================================
        // 建议在真实项目中增加缓存，防止每次发薪都查全表
        Map<Long, SalaryIncomeType> incomeTypeMap = iSalaryIncomeTypeService.list().stream()
                .collect(Collectors.toMap(SalaryIncomeType::getId, t -> t));
        Map<Long, SalaryDeductionType> deductionTypeMap = iSalaryDeductionTypeService.list().stream()
                .collect(Collectors.toMap(SalaryDeductionType::getId, t -> t));

        // ==========================================
        // 3. 处理薪资档案中的【固定配置项】 (FIXED)
        // ==========================================
        List<SalaryArchiveItem> archiveItems = iSalaryArchiveItemService.list(
                Wrappers.<SalaryArchiveItem>lambdaQuery().eq(SalaryArchiveItem::getArchiveId, archive.getId())
        );

        for (SalaryArchiveItem item : archiveItems) {
            BigDecimal itemAmount = BigDecimal.ZERO;
            String formulaStr = "";

            // 计算绝对金额与透明化公式
            // 1. 纯固定金额 (如：通讯补贴、社保代扣)
            if (item.getCalcType() == 1) {
                itemAmount = item.getAmount() != null ? item.getAmount() : BigDecimal.ZERO;
                formulaStr = "固定金额配置";
            }
            // 2. 按基数比例 (如：公积金 8%)
            else if (item.getCalcType() == 2) {
                BigDecimal calcBase = (item.getBaseAmount() != null && item.getBaseAmount().compareTo(BigDecimal.ZERO) > 0)
                        ? item.getBaseAmount() : baseSalary;
                BigDecimal ratio = item.getRatio() != null ? item.getRatio() : BigDecimal.ZERO;
                itemAmount = calcBase.multiply(ratio).setScale(2, RoundingMode.HALF_UP);
                formulaStr = String.format("基数 %s × 比例 %s%%", calcBase, ratio.multiply(new BigDecimal("100")).setScale(2));
            }// 🌟 3. 新增：按出勤天数折算的固定额度 (如：餐补、按比例发放的全勤奖等)
            else if (item.getCalcType() == 3) {
                BigDecimal standardAmount = item.getAmount() != null ? item.getAmount() : BigDecimal.ZERO;
                // 丝滑计算：标准额 × (出勤天数 ÷ 计薪天数)
                itemAmount = standardAmount.multiply(attendanceDays)
                        .divide(monthDays, 2, RoundingMode.HALF_UP);
                formulaStr = String.format("标准额 %s × (出勤 %s ÷ 计薪 %s)",
                        standardAmount, attendanceDays, monthDays);
            }

            // 翻译名称与分类，并累加总额
            String itemName = "未知项";
            String category = "未分类";
            if (item.getItemType() == 1) {
                incomeTotal = incomeTotal.add(itemAmount);
                SalaryIncomeType dict = incomeTypeMap.get(item.getTypeId());
                if (dict != null) {
                    itemName = dict.getTypeName();
                    category = dict.getCategoryName();
                }
            } else {
                deductionTotal = deductionTotal.add(itemAmount);
                SalaryDeductionType dict = deductionTypeMap.get(item.getTypeId());
                if (dict != null) {
                    itemName = dict.getTypeName();
                    category = dict.getCategoryName();
                }
            }

            // --- 写入档案固定项明细 ---
            snapshotItems.add(SalaryDetailItemDTO.builder()
                    .itemName(itemName)
                    .amount(itemAmount)
                    .itemType(item.getItemType())
                    .category(category)
                    .source("FIXED")
                    .formula(formulaStr)
                    .build());
        }

        // ==========================================
        // 4. 处理当月【临时变动项】 (VARIABLE)
        // ==========================================
        List<SalaryIncomeDetail> periodIncomes = iSalaryIncomeDetailService.list(
                Wrappers.<SalaryIncomeDetail>lambdaQuery().eq(SalaryIncomeDetail::getPeriodId, period.getId())
        );
        for (SalaryIncomeDetail pInc : periodIncomes) {
            incomeTotal = incomeTotal.add(pInc.getAmount());
            snapshotItems.add(SalaryDetailItemDTO.builder()
                    .itemName(pInc.getIncomeTypeName()) // 你的表结构里直接冗余了这个，很赞
                    .amount(pInc.getAmount())
                    .itemType(1)
                    .category(pInc.getCategoryName())
                    .source("VARIABLE")
                    .formula(pInc.getRemark() != null ? pInc.getRemark() : "当月临时导入")
                    .build());
        }

        List<SalaryDeductionDetail> periodDeductions = iSalaryDeductionDetailService.list(
                Wrappers.<SalaryDeductionDetail>lambdaQuery().eq(SalaryDeductionDetail::getPeriodId, period.getId())
        );
        for (SalaryDeductionDetail pDed : periodDeductions) {
            deductionTotal = deductionTotal.add(pDed.getAmount());
            snapshotItems.add(SalaryDetailItemDTO.builder()
                    .itemName(pDed.getDeductionTypeName())
                    .amount(pDed.getAmount())
                    .itemType(2)
                    .category(pDed.getCategoryName())
                    .source("VARIABLE")
                    .formula(pDed.getRemark() != null ? pDed.getRemark() : "当月临时导入")
                    .build());
        }

        // ==========================================
        // 5. 最终实发计算与数据入库
        // ==========================================
        BigDecimal finalSalary = incomeTotal.subtract(deductionTotal);
        // 兜底防御，防止扣成负数
        if (finalSalary.compareTo(BigDecimal.ZERO) < 0) {
            finalSalary = BigDecimal.ZERO;
        }

        // 🌟 组装最终 JSON 快照
        SalarySnapshotDTO finalSnapshot = new SalarySnapshotDTO();
        finalSnapshot.setMonthDays(monthDays);
        finalSnapshot.setAttendanceDays(attendanceDays);
        finalSnapshot.setBaseSalary(baseSalary);
        finalSnapshot.setItems(snapshotItems); // 注入明细列表

        SalaryPaymentRecord record = new SalaryPaymentRecord();
        record.setSummaryId(summaryId);
        record.setEmployeeId(archive.getEmployeeId());
        record.setArchiveId(archive.getId());
        record.setBaseSalary(proratedBaseSalary);
        record.setIncomeTotal(incomeTotal);
        record.setDeductionTotal(deductionTotal);
        record.setFinalSalary(finalSalary);
        record.setIsManual(0);
        // 🌟 使用 Fastjson2 序列化对象写入数据库
        record.setDetailJson(com.alibaba.fastjson2.JSON.toJSONString(finalSnapshot));

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

        int successCount = 0;
        int failCount = 0;

        for (SalaryPeriod period : periods) {
            // 🌟 核心防线：单人核算独立 Try-Catch，绝不能让一个人报错卡死全公司发薪
            try {
                // 2. 获取对应的汇总单 ID
                SalarySummary summary = iSalarySummaryService.getOne(
                        Wrappers.<SalarySummary>lambdaQuery().eq(SalarySummary::getPeriodId, period.getId())
                );
                if (summary == null) {
                    log.warn("周期ID {} 缺失汇总单，跳过", period.getId());
                    continue;
                }

                // 3. 获取员工核算月对应的生效档案
                SalaryArchiveVO archive = iSalaryArchiveService.getCurrentArchive(period.getEmployeeId());
                if (archive == null) {
                    log.warn("员工ID {} 缺失生效薪资档案，跳过核算", period.getEmployeeId());
                    continue;
                }

                // 4. 清理旧账，保证幂等性
                iSalaryPaymentRecordService.remove(
                        Wrappers.<SalaryPaymentRecord>lambdaQuery().eq(SalaryPaymentRecord::getSummaryId, summary.getId())
                );

                // 5. 执行单人核算
                this.createRecordByCalculation(summary.getId(), archive, period);
                successCount++;

            } catch (Exception e) {
                failCount++;
                // 打印出具体是哪个员工报错，方便实施人员去修改该员工档案
                log.error("❌ 员工ID {} 核算异常: {}", period.getEmployeeId(), e.getMessage(), e);
            }
        }

        log.info("✅ [薪资引擎] {} 月份全员核算任务执行完毕！总计 {} 人，成功 {} 人，失败 {} 人",
                settlementMonth, periods.size(), successCount, failCount);
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