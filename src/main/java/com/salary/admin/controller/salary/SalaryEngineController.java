package com.salary.admin.controller.salary;

import com.salary.admin.common.ApiResult;
import com.salary.admin.model.dto.engine.SalaryCalcBatchReqDTO;
import com.salary.admin.model.dto.engine.SalaryCalcSingleReqDTO;
import com.salary.admin.model.dto.salary.summary.SummaryInitReqDTO;
import com.salary.admin.model.vo.salary.summary.SalarySummaryVO;
import com.salary.admin.service.salary.ISalaryCoreEngine;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 薪资核心引擎 前端控制器
 * </p>
 * * 负责暴露生命周期流转相关的核心入口：建账、核算
 *
 * @author system
 * @since 2026-03-31
 */
@Tag(name = "薪资核心计算引擎")
@RestController
@RequestMapping("/api/salary/engine")
@Slf4j
@RequiredArgsConstructor
public class SalaryEngineController {

    private final ISalaryCoreEngine coreEngine;

    /**
     * 【生命周期：1. 建账】
     */
    @PostMapping("/initAccount")
    @Operation(summary = "初始化本月账套", description = "自动对齐周期表并生成未核算的空壳汇总单据")
    public ApiResult<Boolean> initAccount(@Validated @RequestBody SummaryInitReqDTO reqDTO) {
        log.info("接收到建账指令，目标月份：{}", reqDTO.getSettlementMonth());
        boolean result = coreEngine.initSummaryAccount(reqDTO);
        return ApiResult.successResult(result);
    }

    /**
     * 【生命周期：2. 核算 (单人预览)】
     */
    @PostMapping("/calc/preview")
    @Operation(summary = "单人核算数据实时预览", description = "仅在内存中试算，不落库，供HR在发薪台弹窗中核对")
    public ApiResult<SalarySummaryVO> previewSingle(@Validated @RequestBody SalaryCalcSingleReqDTO reqDTO) {
        log.info("接收到单人薪资核算预览指令，待核算汇总单ID：{}", reqDTO.getSummaryId());
        // 💡 这里假设你的 coreEngine 提供了一个名为 previewCalculate 的方法返回 VO
        SalarySummaryVO previewVO = coreEngine.previewCalculate(reqDTO);
        return ApiResult.successResult(previewVO);
    }
    /**
     * 【生命周期：3. 核算 (单人落盘)】
     */
    @PostMapping("/calc/single")
    @Operation(summary = "执行单人薪资核算", description = "触发引擎底层的瀑布流管道计算，更新并回写明细与汇总数据")
    public ApiResult<Boolean> calculateSingle(@Validated @RequestBody SalaryCalcSingleReqDTO reqDTO) {
        log.info("接收到单人薪资核算落盘指令，待核算汇总单ID：{}", reqDTO.getSummaryId());
        coreEngine.calculateEmployeeSalary(reqDTO);
        return ApiResult.successResult(true);
    }
    /**
     * 【生命周期：2. 核算 (批量)】
     */
    @PostMapping("/calc/batch")
    @Operation(summary = "批量执行薪资核算", description = "由发薪台触发，对选中的员工单据进行批量规则运算")
    public ApiResult<Boolean> calculateBatch(@Validated @RequestBody SalaryCalcBatchReqDTO reqDTO) {
        // 现在我们直接接收的是汇总单据(账套)的 ID 集合
        log.info("接收到批量薪资核算指令，待核算单据数量：{}",
                reqDTO.getSummaryIds() != null ? reqDTO.getSummaryIds().size() : 0);

        coreEngine.calculateBatchSalary(reqDTO);
        return ApiResult.successResult(true);
    }
}