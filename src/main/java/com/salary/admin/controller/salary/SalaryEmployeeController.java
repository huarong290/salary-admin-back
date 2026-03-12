package com.salary.admin.controller.salary;

import com.salary.admin.annotation.Loggable;
import com.salary.admin.common.ApiResult;
import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.salary.employee.EmployeeAddReqDTO;
import com.salary.admin.model.dto.salary.employee.EmployeeEditReqDTO;
import com.salary.admin.model.dto.salary.employee.EmployeeQueryReqDTO;
import com.salary.admin.model.vo.salary.employee.EmployeeOptionVO;
import com.salary.admin.model.vo.salary.employee.EmployeeVO;
import com.salary.admin.service.salary.ISalaryEmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 员工档案信息 前端控制器
 * </p>
 *
 * @author system
 * @since 2026-03-11
 */
@RestController
@RequestMapping("/api/salary/employee")
@Tag(name = "薪资管理-员工档案")
@Slf4j
public class SalaryEmployeeController {

    @Autowired
    private ISalaryEmployeeService iSalaryEmployeeService;

    @PostMapping("/add")
    @Operation(summary = "新增员工档案")
    @Loggable(title = "员工档案-新增")
    public ApiResult<Long> add(@Validated @RequestBody EmployeeAddReqDTO reqDTO) {
        return ApiResult.successResult(iSalaryEmployeeService.addEmployee(reqDTO));
    }

    @PutMapping("/edit")
    @Operation(summary = "修改员工档案")
    @Loggable(title = "员工档案-修改")
    public ApiResult<Boolean> edit(@Validated @RequestBody EmployeeEditReqDTO reqDTO) {
        return ApiResult.successResult(iSalaryEmployeeService.editEmployee(reqDTO));
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询员工列表")
    public ApiResult<PageResult<EmployeeVO>> page(@RequestBody EmployeeQueryReqDTO reqDTO) {
        return ApiResult.successResult(iSalaryEmployeeService.selectEmployeePage(reqDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取员工详情")
    public ApiResult<EmployeeVO> detail(@PathVariable("id") Long id) {
        return ApiResult.successResult(iSalaryEmployeeService.getEmployeeDetail(id));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "单条删除员工档案")
    @Loggable(title = "员工档案-单条删除")
    public ApiResult<Boolean> delete(
            @PathVariable("id") Long id,
            @Parameter(description = "是否逻辑删除(默认true)") @RequestParam(defaultValue = "true") boolean logicalDelete) {
        return ApiResult.successResult(iSalaryEmployeeService.deleteEmployeeById(id, logicalDelete));
    }

    @DeleteMapping("/delete/batch")
    @Operation(summary = "批量删除员工档案")
    @Loggable(title = "员工档案-批量删除")
    public ApiResult<Boolean> deleteBatch(
            @RequestBody List<Long> ids,
            @Parameter(description = "是否逻辑删除(默认true)") @RequestParam(defaultValue = "true") boolean logicalDelete) {
        return ApiResult.successResult(iSalaryEmployeeService.deleteEmployeeByIds(ids, logicalDelete));
    }

    @GetMapping("/listOption")
    @Operation(summary = "获取在职员工简易列表(用于下拉选择)")
    @Loggable(title = "员工档案-获取在职员工简易列表(用于下拉选择)")
    public ApiResult<List<EmployeeOptionVO>> listOption(
            @Parameter(description = "模糊搜索关键字(姓名/工号)") @RequestParam(required = false) String keyword) {
        return ApiResult.successResult(iSalaryEmployeeService.listOption(keyword));
    }
}