package com.salary.admin.controller.salary;

import cn.hutool.core.util.StrUtil;
import com.salary.admin.annotation.Loggable;
import com.salary.admin.common.ApiResult;
import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.salary.summary.SummaryCalcReqDTO;
import com.salary.admin.model.dto.salary.summary.SummaryQueryReqDTO;
import com.salary.admin.model.entity.salary.SalaryPeriod;
import com.salary.admin.model.vo.salary.summary.SummaryVO;
import com.salary.admin.service.salary.ISalaryCoreEngine;
import com.salary.admin.service.salary.ISalaryPeriodService;
import com.salary.admin.service.salary.ISalarySummaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 薪资汇总与结算表 前端控制器
 * </p>
 *
 * @author system
 * @since 2026-03-11
 */
@RestController
@RequestMapping("/api/salary/summary")
@Tag(name = "薪资引擎-结算汇总")
public class SalarySummaryController {

    @Autowired
    private ISalarySummaryService iSalarySummaryService;

    // 🌟 注入引擎，用于触发核心逻辑
    @Autowired
    private ISalaryCoreEngine iSalaryCoreEngine;
    @Autowired
    private ISalaryPeriodService iSalaryPeriodService;

    @PostMapping("/page")
    @Operation(summary = "分页查询薪资结算单")
    @Loggable(title = "薪资汇总-分页查询薪资结算单")
    public ApiResult<PageResult<SummaryVO>> page(@RequestBody SummaryQueryReqDTO reqDTO) {
        return ApiResult.successResult(iSalarySummaryService.selectSummaryPage(reqDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取薪资结算单详情")
    @Loggable(title = "薪资汇总-获取薪资结算单详情")
    public ApiResult<SummaryVO> detail(@PathVariable("id") Long id) {
        return ApiResult.successResult(iSalarySummaryService.getSummaryDetail(id));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "单条删除薪资结算单")
    @Loggable(title = "薪资汇总-单条删除")
    public ApiResult<Boolean> delete(
            @PathVariable("id") Long id,
            @Parameter(description = "是否逻辑删除(默认true)") @RequestParam(defaultValue = "true") boolean logicalDelete) {
        return ApiResult.successResult(iSalarySummaryService.deleteById(id, logicalDelete));
    }

    @DeleteMapping("/delete/batch")
    @Operation(summary = "批量删除薪资结算单")
    @Loggable(title = "薪资汇总-批量删除")
    public ApiResult<Boolean> deleteBatch(
            @RequestBody List<Long> ids,
            @Parameter(description = "是否逻辑删除(默认true)") @RequestParam(defaultValue = "true") boolean logicalDelete) {
        return ApiResult.successResult(iSalarySummaryService.deleteByIds(ids, logicalDelete));
    }

    @PostMapping("/calculate") // 🌟 路径与前端 api 保持一致
    @Operation(summary = "触发薪资引擎核算")
    @Loggable(title = "薪资引擎-核算")
    public ApiResult<Void> executeSettlement(@RequestBody SummaryCalcReqDTO reqDTO) {
        String targetMonth = reqDTO.getSettlementMonth();

        // 如果前端只传了 periodId，我们需要查出它属于哪个月
        if (StrUtil.isBlank(targetMonth) && reqDTO.getPeriodId() != null) {
            SalaryPeriod period = iSalaryPeriodService.getById(reqDTO.getPeriodId());
            if (period == null) {
                return ApiResult.failResult("指定的薪资周期不存在");
            }
            targetMonth = period.getSettlementMonth();
        }

        if (StrUtil.isBlank(targetMonth)) {
            return ApiResult.failResult("结算月份或周期ID不能为空");
        }

        // 调用引擎：执行全员核算
        // 注意：你可以扩展引擎方法，把 remark 也传进去存入 Summary 表
        iSalaryCoreEngine.executeGlobalSettlement(targetMonth);

        return ApiResult.defaultSuccessResult();
    }
}
