package com.salary.admin.service.impl.salary;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.salary.admin.exception.BusinessException;
import com.salary.admin.model.dto.engine.SalaryCalcBatchReqDTO;
import com.salary.admin.model.dto.engine.SalaryCalcSingleReqDTO;
import com.salary.admin.model.dto.salary.period.PeriodBatchInitReqDTO;
import com.salary.admin.model.dto.salary.summary.SummaryInitReqDTO;
import com.salary.admin.model.entity.salary.SalaryEmployee;
import com.salary.admin.model.entity.salary.SalaryPeriod;
import com.salary.admin.model.entity.salary.SalarySummary;
import com.salary.admin.model.vo.salary.summary.SalarySummaryVO;
import com.salary.admin.service.impl.salary.engine.SalaryPersistProcessor;
import com.salary.admin.service.impl.salary.engine.SalaryPreviewProcessor;
import com.salary.admin.service.salary.ISalaryCoreEngine;
import com.salary.admin.service.salary.ISalaryEmployeeService;
import com.salary.admin.service.salary.ISalaryPeriodService;
import com.salary.admin.service.salary.ISalarySummaryService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 薪资核心引擎实现类
 * <p>
 * 核心定位：
 * - 作为薪资模块的“总调度室”，负责跨表、跨业务的复杂逻辑编排。
 * 架构特性：图纸化编排、Context变量透传、动态Aviator阻断、强类型DTO入参
 * * @author system
 * @since 2026-03-31
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SalaryCoreEngineImpl implements ISalaryCoreEngine {

    // 基础业务 Service
    private final ISalaryEmployeeService iSalaryEmployeeService;
    private final ISalaryPeriodService iSalaryPeriodService;
    private final ISalarySummaryService iSalarySummaryService;

    private final SalaryPersistProcessor salaryPersistProcessor;
    private final SalaryPreviewProcessor salaryPreviewProcessor;

    // 方案 A：在类中注入自身的代理对象（推荐，最优雅）
    @Resource
    @Lazy
    private ISalaryCoreEngine iSalaryCoreEngineSelfProxy;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean initSummaryAccount(SummaryInitReqDTO reqDTO) {
        String month = reqDTO.getSettlementMonth();
        List<Long> targetEmpIds = reqDTO.getEmployeeIds();

        // 1. 初始化底层周期 (调用已有逻辑)
        PeriodBatchInitReqDTO periodReq = new PeriodBatchInitReqDTO();
        periodReq.setSettlementMonth(month);
        periodReq.setEmployeeIds(targetEmpIds);
        iSalaryPeriodService.batchInitPeriodsOnly(periodReq);

        // 2. 以 Period 为基准获取本月数据
        LambdaQueryWrapper<SalaryPeriod> periodWrapper = new LambdaQueryWrapper<SalaryPeriod>()
                .eq(SalaryPeriod::getSettlementMonth, month);
        if (CollUtil.isNotEmpty(targetEmpIds)) {
            periodWrapper.in(SalaryPeriod::getEmployeeId, targetEmpIds);
        }
        List<SalaryPeriod> allPeriods = iSalaryPeriodService.list(periodWrapper);
        if (CollUtil.isEmpty(allPeriods)) return false;

        // 3. Diff 查出还没有 Summary 的周期
        List<Long> periodIds = allPeriods.stream().map(SalaryPeriod::getId).collect(Collectors.toList());
        Set<Long> existSummaryPeriodIds = iSalarySummaryService.list(new LambdaQueryWrapper<SalarySummary>()
                        .in(SalarySummary::getPeriodId, periodIds))
                .stream().map(SalarySummary::getPeriodId).collect(Collectors.toSet());

        List<SalaryPeriod> needInitPeriods = allPeriods.stream()
                .filter(p -> !existSummaryPeriodIds.contains(p.getId()))
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(needInitPeriods)) return true; // 都存在则直接返回成功

        // 4. 获取员工快照
        Set<Long> empIds = needInitPeriods.stream().map(SalaryPeriod::getEmployeeId).collect(Collectors.toSet());
        Map<Long, SalaryEmployee> empMap = iSalaryEmployeeService.listByIds(empIds).stream()
                .collect(Collectors.toMap(SalaryEmployee::getId, e -> e));

        // 5. 构建并落库 Summary 空壳数据
        List<SalarySummary> newSummaries = needInitPeriods.stream().map(p -> {
            SalaryEmployee emp = empMap.get(p.getEmployeeId());
            SalarySummary summary = new SalarySummary();
            summary.setEmployeeId(p.getEmployeeId());
            summary.setPeriodId(p.getId());
            summary.setSettlementMonth(month);

            if (emp != null) {
                summary.setEmployeeCode(emp.getEmployeeCode());
                summary.setEmployeeName(emp.getEmployeeName());
            } else {
                summary.setEmployeeCode("UNKNOWN");
                summary.setEmployeeName("未知");
            }
            // 初始状态设定
            summary.setCalcStatus(0);
            summary.setPaymentStatus(0);
            summary.setLockFlag(0);
            summary.setCalcVersion(1);

            // 金额清零
            summary.setIncomeTotal(BigDecimal.ZERO);
            summary.setDeductionTotal(BigDecimal.ZERO);
            summary.setTaxTotal(BigDecimal.ZERO);
            summary.setGrossSalary(BigDecimal.ZERO);
            summary.setNetSalary(BigDecimal.ZERO);

            return summary;
        }).collect(Collectors.toList());

        return iSalarySummaryService.saveBatch(newSummaries, 500);
    }
    /**
     * 单人核算数据实时预览 (仅内存计算，不落库)
     * 逻辑与 calculateEmployeeSalary 高度复用，但剥离了持久化操作
     */
    @Override
    public SalarySummaryVO previewCalculate(SalaryCalcSingleReqDTO reqDTO) {
        // 1. 基础校验补全
        SalarySummary summary = iSalarySummaryService.getById(reqDTO.getSummaryId());
        if (summary == null) throw new BusinessException("薪资账套数据不存在！");
        // 将账套上的周期 ID 和员工 ID 补全到请求上下文中，防止底层查询查到 null
        if (reqDTO.getPeriodId() == null || reqDTO.getEmployeeId() == null) {
            reqDTO.setPeriodId(summary.getPeriodId());
            reqDTO.setEmployeeId(summary.getEmployeeId());
        }
        // 2. 路由到预览处理器
        return salaryPreviewProcessor.process(reqDTO);
    }
    /**
     * 执行单人当月薪资核算 (瀑布流管道计算核心)
     *
     * @param reqDTO 单人核算参数指令
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void calculateEmployeeSalary(SalaryCalcSingleReqDTO reqDTO) {
        // 1. 参数补全逻辑 (如果直接调单人核算API，这里自动补全)
        if (reqDTO.getPeriodId() == null || reqDTO.getEmployeeId() == null) {
            SalarySummary summary = iSalarySummaryService.getById(reqDTO.getSummaryId());
            if (summary == null) throw new BusinessException("薪资账套数据不存在！");
            if (summary.getLockFlag() == 1) throw new BusinessException("该工资单已锁定，禁止重算！");

            reqDTO.setPeriodId(summary.getPeriodId());
            reqDTO.setEmployeeId(summary.getEmployeeId());
        }

        // 2. 路由到落库处理器 (内部天然开启了本地事务)
        salaryPersistProcessor.process(reqDTO);

    }

    /**
     * 批量执行薪资核算 (发薪台触发)
     *
     * @param reqDTO 批量核算参数指令
     */
    @Override
    public void calculateBatchSalary(SalaryCalcBatchReqDTO reqDTO) {
        // 批量查出单据
        List<SalarySummary> summaryList = iSalarySummaryService.listByIds(reqDTO.getSummaryIds());
        if (CollUtil.isEmpty(summaryList)) {
            throw new BusinessException("未找到匹配的核算账套数据！");
        }

        int successCount = 0;
        int failCount = 0;

        for (SalarySummary summary : summaryList) {
            // 核心拦截：已锁定的工资单绝对禁止重新核算
            if (summary.getLockFlag() == 1) {
                log.warn("⚠️ 员工 [{}] 的工资单已锁定，已跳过引擎重算。", summary.getEmployeeName());
                continue;
            }

            try {
                // 利用 @Accessors(chain = true) 优雅地构造单人请求
                SalaryCalcSingleReqDTO singleReq = new SalaryCalcSingleReqDTO()
                        .setSummaryId(summary.getId())
                        .setPeriodId(summary.getPeriodId())
                        .setEmployeeId(summary.getEmployeeId())
                        .setPipelineCode(reqDTO.getPipelineCode())
                        .setPipelineVersion(reqDTO.getPipelineVersion());

                // 调用单人核算 (内层包含独立事务控制)
                iSalaryCoreEngineSelfProxy.calculateEmployeeSalary(singleReq);
                successCount++;
            } catch (Exception e) {
                // 异常隔离：批量处理时，其中一人报错，不影响其他人的计算进度
                log.error("❌ 员工 [{}] 核算失败: {}", summary.getEmployeeName(), e.getMessage());

                // 将失败状态写回主表
                summary.setCalcStatus(2); // 2-计算失败
                summary.setRemark("核算报错: " + e.getMessage());
                iSalarySummaryService.updateById(summary);
                failCount++;
            }
        }

        log.info("📊 批量核算任务结束。成功: {} 条，失败: {} 条", successCount, failCount);
        if (failCount > 0) {
            throw new BusinessException("核算结束。成功 " + successCount + " 条，但有 " + failCount + " 条发生异常，请查看详情。");
        }
    }
}