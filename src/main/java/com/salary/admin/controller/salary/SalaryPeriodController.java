package com.salary.admin.controller.salary;

import com.salary.admin.annotation.Loggable;
import com.salary.admin.common.ApiResult;
import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.salary.period.PeriodAddReqDTO;
import com.salary.admin.model.dto.salary.period.PeriodBatchInitReqDTO;
import com.salary.admin.model.dto.salary.period.PeriodEditReqDTO;
import com.salary.admin.model.dto.salary.period.PeriodQueryReqDTO;
import com.salary.admin.model.entity.salary.SalaryPeriod;
import com.salary.admin.model.vo.salary.period.PeriodBatchInitResultVO;
import com.salary.admin.model.vo.salary.period.PeriodOptionVO;
import com.salary.admin.model.vo.salary.period.PeriodVO;
import com.salary.admin.service.salary.ISalaryCoreEngine;
import com.salary.admin.service.salary.ISalaryPeriodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 薪资周期信息表 前端控制器
 * </p>
 *
 * @author system
 * @since 2026-03-11
 */
@RestController
@RequestMapping("/api/salary/period")
@Tag(name = "薪资管理-薪资周期")
public class SalaryPeriodController {

    @Autowired
    private ISalaryPeriodService iSalaryPeriodService;

    @PostMapping("/add")
    @Operation(summary = "新增单条薪资周期")
    @Loggable(title = "薪资周期-新增")
    public ApiResult<Long> add(@Validated @RequestBody PeriodAddReqDTO reqDTO) {
        return ApiResult.successResult(iSalaryPeriodService.addPeriod(reqDTO));
    }

    /**
     * 批量初始化薪资周期
     * 场景：HR 在周期管理页面点击“初始化本月账套”
     */
    @Operation(summary = "批量初始化薪资周期")
    @PostMapping("/batch-init")
    public ApiResult<PeriodBatchInitResultVO> batchInit(@RequestBody PeriodBatchInitReqDTO reqDTO) {
        // 调用我们重构后的纯净初始化方法
        PeriodBatchInitResultVO resultVO = iSalaryPeriodService.batchInitPeriodsOnly(reqDTO);
        return ApiResult.successResult(resultVO);
    }

    @PutMapping("/edit")
    @Operation(summary = "修改薪资周期")
    @Loggable(title = "薪资周期-修改")
    public ApiResult<Boolean> edit(@Validated @RequestBody PeriodEditReqDTO reqDTO) {
        return ApiResult.successResult(iSalaryPeriodService.editPeriod(reqDTO));
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询周期列表")
    @Loggable(title = "薪资周期-分页查询周期列表")
    public ApiResult<PageResult<PeriodVO>> page(@RequestBody PeriodQueryReqDTO reqDTO) {
        return ApiResult.successResult(iSalaryPeriodService.selectPeriodPage(reqDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取周期详情")
    @Loggable(title = "薪资周期-获取周期详情")
    public ApiResult<PeriodVO> detail(@PathVariable("id") Long id) {
        return ApiResult.successResult(iSalaryPeriodService.getPeriodDetail(id));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除薪资周期")
    @Loggable(title = "薪资周期-单条删除")
    public ApiResult<Boolean> delete(
            @PathVariable("id") Long id,
            @Parameter(description = "是否逻辑删除") @RequestParam(defaultValue = "true") boolean logicalDelete) {
        return ApiResult.successResult(iSalaryPeriodService.deletePeriodById(id, logicalDelete));
    }

    @DeleteMapping("/delete/batch")
    @Operation(summary = "批量删除薪资周期")
    @Loggable(title = "薪资周期-批量删除")
    public ApiResult<Boolean> deleteBatch(
            @RequestBody List<Long> ids,
            @Parameter(description = "是否逻辑删除") @RequestParam(defaultValue = "true") boolean logicalDelete) {
        return ApiResult.successResult(iSalaryPeriodService.deletePeriodByIds(ids, logicalDelete));
    }

    @GetMapping("/listOption")
    @Operation(summary = "获取可选的结算月份列表")
    @Loggable(title = "薪资周期-获取可选的结算月份列表")
    public ApiResult<List<PeriodOptionVO>> listOption() {
        // 逻辑：查询数据库中已存在的 settlementMonth，并进行去重排序
        // SELECT DISTINCT settlementMonth FROM salary_period ORDER BY settlementMonth DESC
        return ApiResult.successResult(iSalaryPeriodService.listOption());
    }

    @GetMapping("/listOptionByEmployee")
    @Operation(summary = "获取指定员工的结算月份列表 (无分页)")
    @Loggable(title = "薪资周期-获取指定员工月份列表")
    public ApiResult<List<PeriodOptionVO>> listOptionByEmployee(@RequestParam("employeeId") Long employeeId) {
        return ApiResult.successResult(iSalaryPeriodService.listOptionByEmployee(employeeId));
    }
}