package com.salary.admin.controller.salary;

import com.salary.admin.annotation.Loggable;
import com.salary.admin.common.ApiResult;
import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.salary.period.PeriodAddReqDTO;
import com.salary.admin.model.dto.salary.period.PeriodBatchInitReqDTO;
import com.salary.admin.model.dto.salary.period.PeriodEditReqDTO;
import com.salary.admin.model.dto.salary.period.PeriodQueryReqDTO;
import com.salary.admin.model.vo.salary.period.PeriodBatchInitResultVO;
import com.salary.admin.model.vo.salary.period.PeriodOptionVO;
import com.salary.admin.model.vo.salary.period.PeriodVO;
import com.salary.admin.service.salary.ISalaryPeriodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 薪资周期信息表 前端控制器
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
@RestController
@RequestMapping("/api/salary/period")
@Tag(name = "薪资周期管理", description = "员工每月薪资考勤结算周期的初始化与维护")
@Slf4j
@RequiredArgsConstructor
public class SalaryPeriodController {

    private final ISalaryPeriodService periodService;

    @PostMapping("/page")
    @Operation(summary = "分页查询薪资周期")
    public ApiResult<PageResult<PeriodVO>> getPage(@RequestBody PeriodQueryReqDTO reqDTO) {
        return ApiResult.successResult(periodService.selectPeriodPage(reqDTO));
    }

    @PostMapping("/add")
    @Operation(summary = "单条新增薪资周期")
    @Loggable(title = "薪资周期-单条新增", logRequest = true)
    public ApiResult<Long> addPeriod(@Validated @RequestBody PeriodAddReqDTO reqDTO) {
        return ApiResult.successResult(periodService.addPeriod(reqDTO));
    }

    @PutMapping("/edit")
    @Operation(summary = "修改薪资周期信息")
    @Loggable(title = "薪资周期-修改", logRequest = true)
    public ApiResult<Boolean> editPeriod(@Validated @RequestBody PeriodEditReqDTO reqDTO) {
        return ApiResult.successResult(periodService.editPeriod(reqDTO));
    }

    @GetMapping("/detail/{id}")
    @Operation(summary = "获取周期详情")
    public ApiResult<PeriodVO> getDetail(@PathVariable Long id) {
        return ApiResult.successResult(periodService.getPeriodDetail(id));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除薪资周期")
    @Loggable(title = "薪资周期-删除")
    public ApiResult<Boolean> deletePeriod(
            @PathVariable Long id,
            @RequestParam(defaultValue = "true") boolean logicalDelete) {
        return ApiResult.successResult(periodService.deletePeriodById(id, logicalDelete));
    }

    @PostMapping("/batch-init")
    @Operation(summary = "批量初始化薪资周期", description = "为指定员工批量创建本月账套，具备幂等性")
    @Loggable(title = "薪资周期-批量初始化", logRequest = true)
    public ApiResult<PeriodBatchInitResultVO> batchInitPeriods(@Validated @RequestBody PeriodBatchInitReqDTO reqDTO) {
        return ApiResult.successResult(periodService.batchInitPeriodsOnly(reqDTO));
    }

    @GetMapping("/options")
    @Operation(summary = "获取已存在的结算月份列表", description = "用于前端账套下拉筛选")
    public ApiResult<List<PeriodOptionVO>> listOptions() {
        return ApiResult.successResult(periodService.listOption());
    }
}