package com.salary.admin.service.impl.salary;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.salary.admin.model.dto.salary.period.PeriodAddReqDTO;
import com.salary.admin.model.dto.salary.period.PeriodBatchInitReqDTO;
import com.salary.admin.model.dto.salary.snapshot.SalaryDetailItemDTO;
import com.salary.admin.model.dto.salary.snapshot.SalarySnapshotDTO;
import com.salary.admin.model.entity.salary.*;
import com.salary.admin.model.vo.salary.archive.SalaryArchiveVO;
import com.salary.admin.model.vo.salary.period.PeriodBatchInitResultVO;
import com.salary.admin.model.vo.salary.summary.SummaryVO;
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

    private final ISalaryEmployeeService iSalaryEmployeeService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public PeriodBatchInitResultVO batchInitPeriods(PeriodBatchInitReqDTO reqDTO) {
        log.info("🚀 [薪资引擎] 开始执行 {} 月份批量初始化账套...", reqDTO.getSettlementMonth());

        // 1. 调用底层 Service 完成周期初始化
        PeriodBatchInitResultVO resultVO = iSalaryPeriodService.batchInitPeriodsOnly(reqDTO);

        // 2. 如果有新增周期，进一步初始化汇总单（可选）
        if (resultVO.getSuccessCount() != null && resultVO.getSuccessCount() > 0) {
            List<SalaryPeriod> newPeriods = iSalaryPeriodService.list(
                    Wrappers.<SalaryPeriod>lambdaQuery()
                            .eq(SalaryPeriod::getSettlementMonth, reqDTO.getSettlementMonth())
                            .in(SalaryPeriod::getEmployeeId, reqDTO.getEmployeeIds())
            );
            this.initSummaryForPeriods(newPeriods);
            log.info("✅ 已为 {} 条周期补全了汇总单", newPeriods.size());
        }

        // 3. 返回结果 VO
        log.info("🎯 [薪资引擎] {} 月份账套初始化完成：目标 {} 人，成功 {} 人，跳过 {} 人",
                resultVO.getSettlementMonth(),
                resultVO.getTotalCount(),
                resultVO.getSuccessCount(),
                resultVO.getSkipCount());

        return resultVO;
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

        // 🌟 新增：专门用于记录个税税前可扣除的“五险一金”总额
        BigDecimal socialSecurityDeductionForTax = BigDecimal.ZERO;
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
                // 🌟 优先级：item.base_amount > archive.base_salary
                BigDecimal calcBase = (item.getBaseAmount() != null && item.getBaseAmount().compareTo(BigDecimal.ZERO) > 0)? item.getBaseAmount() : baseSalary;

                BigDecimal ratio = item.getRatio() != null ? item.getRatio() : BigDecimal.ZERO;
                itemAmount = calcBase.multiply(ratio).setScale(2, RoundingMode.HALF_UP);
//                formulaStr = String.format("基数 %s × 比例 %s%%", calcBase, ratio.multiply(new BigDecimal("100")).setScale(2));
                formulaStr = String.format("基数 %s × 比例 %s%%", calcBase, ratio.multiply(new BigDecimal("100")).stripTrailingZeros().toPlainString());
            }// 🌟 3. 新增：按出勤天数折算的固定额度 (如：餐补、按比例发放的全勤奖等)
            else if (item.getCalcType() == 3) {
                BigDecimal standardAmount = item.getAmount() != null ? item.getAmount() : BigDecimal.ZERO;
                // 丝滑计算：标准额 × (出勤天数 ÷ 计薪天数)
                itemAmount = standardAmount.multiply(attendanceDays).divide(monthDays, 2, RoundingMode.HALF_UP);
                formulaStr = String.format("标准额 %s × (出勤 %s ÷ 计薪 %s)",standardAmount, attendanceDays, monthDays);

            }

            // 翻译名称与分类，并累加总额
            String itemName = "未知项";
            String category = "未分类";

            // 收入项
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
                    // 🌟 关键判定：如果是社保或公积金，累加到税前扣除额中
                    // 这里可以通过名称判定，或者给字典表增加一个标记位 `is_tax_deductible`
                    if (itemName.contains("社保") || itemName.contains("保险") || itemName.contains("公积金")) {
                        socialSecurityDeductionForTax = socialSecurityDeductionForTax.add(itemAmount);
                    }
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
        // 🌟 5.0 个人所得税自动核算 (根据前面累加的结果计算)
        // ==========================================
        // 公式：(应发总额 - 税前可扣除五险一金 - 5000起征点)
        BigDecimal taxableIncome = incomeTotal.subtract(socialSecurityDeductionForTax).subtract(new BigDecimal("5000"));
        BigDecimal personalTax = BigDecimal.ZERO;

        if (taxableIncome.compareTo(BigDecimal.ZERO) > 0) {
            // 匹配简易税率表 (7级超额累进)
            if (taxableIncome.compareTo(new BigDecimal("3000")) <= 0) {
                personalTax = taxableIncome.multiply(new BigDecimal("0.03")).setScale(2, RoundingMode.HALF_UP);
            } else if (taxableIncome.compareTo(new BigDecimal("12000")) <= 0) {
                personalTax = taxableIncome.multiply(new BigDecimal("0.1")).subtract(new BigDecimal("210")).setScale(2, RoundingMode.HALF_UP);
            } else {
                personalTax = taxableIncome.multiply(new BigDecimal("0.2")).subtract(new BigDecimal("1410")).setScale(2, RoundingMode.HALF_UP);
            }
        }

        if (personalTax.compareTo(BigDecimal.ZERO) > 0) {
            deductionTotal = deductionTotal.add(personalTax);
            snapshotItems.add(SalaryDetailItemDTO.builder()
                    .itemName("个人所得税").amount(personalTax).itemType(2)
                    .category("法定扣款").source("SYSTEM_CALC")
                    .formula("应纳税所得额 × 适用税率 - 速算扣除数").build());
        }
        // ==========================================
        // 6. 最终实发计算与数据入库
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

    /**
     * 生产环境建议：异步执行 (Async)
     * 如果你的公司员工超过 1000 人，这个方法执行时间可能会超过 30 秒，导致前端接口超时（Timeout）
     * @param settlementMonth
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeGlobalSettlement(String settlementMonth) {
        log.info("🚀 [薪资引擎] 开始执行 {} 月份全员核算任务", settlementMonth);
        // 🌟 【新增：自动补偿机制】
        // 在算钱之前，先确保本月所有在职员工的“坑位”（Period 和 Summary）已经占好了
        // 如果已经初始化过了，这个方法内部有幂等检查，执行速度极快
        this.initMonthlyBatchForAll(settlementMonth);
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

    /**
     * 场景 B：指定核算 (精准核算某一个或多个周期) =重新考试（重新计算成绩单）
     * 主要用于前端点击“重新核算”时的单人即时核算
     *
     * @param periodIds 周期ID列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeSettlementByPeriods(List<Long> periodIds) {
        if (CollUtil.isEmpty(periodIds)) {
            return;
        }

        log.info("🚀 [薪资引擎] 开始执行精准指定核算任务，目标周期数: {}", periodIds.size());

        // 1. 获取指定的薪资周期
        List<SalaryPeriod> periods = iSalaryPeriodService.listByIds(periodIds);

        for (SalaryPeriod period : periods) {
            try {
                // 2. 获取对应的汇总单 ID
                SalarySummary summary = iSalarySummaryService.getOne(
                        Wrappers.<SalarySummary>lambdaQuery().eq(SalarySummary::getPeriodId, period.getId())
                );
                if (summary == null) {
                    log.warn("周期ID {} 缺失汇总单，跳过", period.getId());
                    continue;
                }

                // 3. 获取员工当前生效的薪资档案
                SalaryArchiveVO archive = iSalaryArchiveService.getCurrentArchive(period.getEmployeeId());
                if (archive == null) {
                    log.warn("员工ID {} 缺失生效薪资档案，跳过核算", period.getEmployeeId());
                    continue;
                }

                // 4. 清理旧账，保证幂等性 (核心风险点：此操作会覆写历史明细)
                iSalaryPaymentRecordService.remove(
                        Wrappers.<SalaryPaymentRecord>lambdaQuery().eq(SalaryPaymentRecord::getSummaryId, summary.getId())
                );

                // 5. 调用核心算薪公式，生成明细快照并持久化
                this.createRecordByCalculation(summary.getId(), archive, period);

                log.info("✅ 员工ID {} 核算成功", period.getEmployeeId());

            } catch (Exception e) {
                log.error("❌ 员工ID {} 核算异常: {}", period.getEmployeeId(), e.getMessage(), e);
            }
        }
    }

    /**
     * 根据薪资周期 ID，同步/刷新对应汇总单的总金额 = 重新核分（把成绩单上的分数重新加一遍报给教务处）
     * <p>
     * 【业务场景与架构约束】：
     * 该方法属于“轻量级且非破坏性”的数据同步操作。
     * 通常用于底层发薪明细（SalaryPaymentRecord）发生人工强制干预（如：财务进行单据微调、强制平账）后，
     * 系统需要将底层的各项实发、应发金额重新汇总求和，并将最新总额反写回顶层的汇总大盘（SalarySummary）中。
     * <p>
     * ⚠️ 注意：此过程【绝对不会】重新拉取员工档案，也【绝对不会】触发薪资引擎的公式重算。
     *
     * @param periodId 薪资周期 ID (关联 salary_period 表的主键)
     */
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

    @Override
    public SummaryVO previewCalculateByPeriod(Long periodId) {
        // 1. 获取前置基础数据
        SalaryPeriod period = iSalaryPeriodService.getById(periodId);
        if (period == null) {
            throw new RuntimeException("指定的薪资周期不存在");
        }
        SalaryArchiveVO archive = iSalaryArchiveService.getCurrentArchive(period.getEmployeeId());
        if (archive == null) {
            throw new RuntimeException("未找到该员工生效的薪资档案");
        }
        SalaryEmployee employee = iSalaryEmployeeService.getById(period.getEmployeeId());

        // 2. 初始化金额累加器
        BigDecimal incomeTotal = BigDecimal.ZERO;
        BigDecimal deductionTotal = BigDecimal.ZERO;

        // 3. 计算底薪与出勤折算
        BigDecimal baseSalary = archive.getBaseSalary() != null ? archive.getBaseSalary() : BigDecimal.ZERO;
        BigDecimal monthDays = (period.getMonthDays() != null && period.getMonthDays().compareTo(BigDecimal.ZERO) > 0)
                ? period.getMonthDays() : new BigDecimal("21.75");
        BigDecimal attendanceDays = period.getAttendanceDays() != null ? period.getAttendanceDays() : BigDecimal.ZERO;

        BigDecimal proratedBaseSalary = baseSalary.divide(monthDays, 4, RoundingMode.HALF_UP)
                .multiply(attendanceDays).setScale(2, RoundingMode.HALF_UP);
        incomeTotal = incomeTotal.add(proratedBaseSalary);

        // 4. 计算全勤奖
        BigDecimal fullAttendanceBonus = archive.getFullAttendanceBonus() != null ? archive.getFullAttendanceBonus() : BigDecimal.ZERO;
        if (fullAttendanceBonus.compareTo(BigDecimal.ZERO) > 0 && Integer.valueOf(1).equals(period.getFullAttendanceFlag())) {
            incomeTotal = incomeTotal.add(fullAttendanceBonus);
        }

        // 5. 计算档案固定项 (FIXED)
        List<SalaryArchiveItem> archiveItems = iSalaryArchiveItemService.list(
                Wrappers.<SalaryArchiveItem>lambdaQuery().eq(SalaryArchiveItem::getArchiveId, archive.getId())
        );
        for (SalaryArchiveItem item : archiveItems) {
            BigDecimal itemAmount = BigDecimal.ZERO;
            if (item.getCalcType() == 1) { // 固定金额
                itemAmount = item.getAmount() != null ? item.getAmount() : BigDecimal.ZERO;
            } else if (item.getCalcType() == 2) { // 比例基数
                BigDecimal calcBase = (item.getBaseAmount() != null && item.getBaseAmount().compareTo(BigDecimal.ZERO) > 0) ? item.getBaseAmount() : baseSalary;
                itemAmount = calcBase.multiply(item.getRatio() != null ? item.getRatio() : BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
            } else if (item.getCalcType() == 3) { // 出勤折算
                BigDecimal standardAmount = item.getAmount() != null ? item.getAmount() : BigDecimal.ZERO;
                itemAmount = standardAmount.multiply(attendanceDays).divide(monthDays, 2, RoundingMode.HALF_UP);
            }

            if (item.getItemType() == 1) incomeTotal = incomeTotal.add(itemAmount);
            else deductionTotal = deductionTotal.add(itemAmount);
        }

        // 6. 计算当月临时变动项 (VARIABLE)
        List<SalaryIncomeDetail> periodIncomes = iSalaryIncomeDetailService.list(Wrappers.<SalaryIncomeDetail>lambdaQuery().eq(SalaryIncomeDetail::getPeriodId, period.getId()));
        for (SalaryIncomeDetail pInc : periodIncomes) incomeTotal = incomeTotal.add(pInc.getAmount());

        List<SalaryDeductionDetail> periodDeductions = iSalaryDeductionDetailService.list(Wrappers.<SalaryDeductionDetail>lambdaQuery().eq(SalaryDeductionDetail::getPeriodId, period.getId()));
        for (SalaryDeductionDetail pDed : periodDeductions) deductionTotal = deductionTotal.add(pDed.getAmount());

        // 7. 计算最终实发金额 (防呆处理不能为负数)
        BigDecimal finalSalary = incomeTotal.subtract(deductionTotal);
        if (finalSalary.compareTo(BigDecimal.ZERO) < 0) finalSalary = BigDecimal.ZERO;

        // 🌟 8. 组装返回给前端的预览视图 (VO)
        SummaryVO previewVO = new SummaryVO();
        previewVO.setEmployeeName(employee != null ? employee.getEmployeeName() : "未知员工");
        previewVO.setSettlementMonth(period.getSettlementMonth());
        previewVO.setSalarySubtotal(incomeTotal);
        previewVO.setSalaryDeductionTotal(deductionTotal);
        previewVO.setSalaryTotal(finalSalary); // 最终核算结果

        return previewVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initMonthlyBatchForAll(String settlementMonth) {
        log.info("🚀 [薪资引擎] 开始执行 {} 月份全员月度建账...", settlementMonth);

        // 1. 获取所有在职员工 (employment_status = 1)
        List<SalaryEmployee> activeEmployees = iSalaryEmployeeService.list(
                Wrappers.<SalaryEmployee>lambdaQuery()
                        .eq(SalaryEmployee::getEmploymentStatus, 1)
                        .eq(SalaryEmployee::getDeleteFlag, 0)
        );

        if (CollUtil.isEmpty(activeEmployees)) {
            log.warn("⚠️ 未找到任何在职员工，建账终止");
            return;
        }

        // 2. 构造日期范围 (基于 settlementMonth 自动计算该月第一天和最后一天)
        // 假设 settlementMonth 格式为 "202405"
        cn.hutool.core.date.DateTime monthDate = cn.hutool.core.date.DateUtil.parse(settlementMonth, "yyyyMM");
        java.time.LocalDate start = cn.hutool.core.date.DateUtil.beginOfMonth(monthDate).toLocalDateTime().toLocalDate();
        java.time.LocalDate end = cn.hutool.core.date.DateUtil.endOfMonth(monthDate).toLocalDateTime().toLocalDate();

        // 3. 筛选出【当前月份还没有周期记录】的员工 ID
        List<Long> allActiveIds = activeEmployees.stream().map(SalaryEmployee::getId).collect(Collectors.toList());

        // 查出本月已存在周期的员工
        List<Long> existPeriodEmpIds = iSalaryPeriodService.list(
                Wrappers.<SalaryPeriod>lambdaQuery()
                        .select(SalaryPeriod::getEmployeeId)
                        .eq(SalaryPeriod::getSettlementMonth, settlementMonth)
                        .in(SalaryPeriod::getEmployeeId, allActiveIds)
        ).stream().map(SalaryPeriod::getEmployeeId).collect(Collectors.toList());

        // 4 得到真正需要创建周期的员工 ID
        List<Long> needCreateIds = allActiveIds.stream()
                .filter(id -> !existPeriodEmpIds.contains(id))
                .collect(Collectors.toList());

        // 5. 如果有需要补全的周期，调用你现有的批量初始化 DTO
        if (CollUtil.isNotEmpty(needCreateIds)) {
            PeriodBatchInitReqDTO initReq = new PeriodBatchInitReqDTO();
            initReq.setSettlementMonth(settlementMonth);
            initReq.setEmployeeIds(needCreateIds);
            initReq.setStartDate(start);
            initReq.setEndDate(end);

            // 🌟 关键点：直接调用你已有的逻辑
            this.batchInitPeriods(initReq);
            log.info("✅ 已为 {} 名新入职或缺失周期的员工补全了账套", needCreateIds.size());
        }else {
            log.info("ℹ️ {} 月份周期已全部存在，无需补全", settlementMonth);
        }

        // 6. 额外防御：检查是否有“有周期但没汇总单”的情况（针对手动删了汇总单的极端情况）
        // 这一步能保证 Summary 页面数据的绝对完整
        List<SalaryPeriod> allPeriods = iSalaryPeriodService.list(
                Wrappers.<SalaryPeriod>lambdaQuery().eq(SalaryPeriod::getSettlementMonth, settlementMonth)
        );

        // 找出没有 Summary 记录的 Period
        List<SalaryPeriod> periodsWithoutSummary = allPeriods.stream().filter(p ->
                !iSalarySummaryService.exists(Wrappers.<SalarySummary>lambdaQuery().eq(SalarySummary::getPeriodId, p.getId()))
        ).collect(Collectors.toList());

        if (CollUtil.isNotEmpty(periodsWithoutSummary)) {
            this.initSummaryForPeriods(periodsWithoutSummary);
            log.info("✅ 补全了 {} 条缺失的汇总单记录", periodsWithoutSummary.size());
        }
    }
}