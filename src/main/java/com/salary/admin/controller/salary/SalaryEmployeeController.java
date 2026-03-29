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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 员工基本信息表 前端控制器
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
@RestController
@RequestMapping("/api/salary/employee")
@Tag(name = "员工档案管理", description = "维护员工基本信息及在职状态")
@Slf4j
@RequiredArgsConstructor
public class SalaryEmployeeController {

    private final ISalaryEmployeeService employeeService;

    @PostMapping("/page")
    @Operation(summary = "分页查询员工列表")
    public ApiResult<PageResult<EmployeeVO>> getPage(@RequestBody EmployeeQueryReqDTO reqDTO) {
        return ApiResult.successResult(employeeService.selectEmployeePage(reqDTO));
    }

    @PostMapping("/add")
    @Operation(summary = "新增员工档案")
    @Loggable(title = "员工档案-新增", logRequest = true)
    public ApiResult<Long> addEmployee(@Validated @RequestBody EmployeeAddReqDTO reqDTO) {
        return ApiResult.successResult(employeeService.addEmployee(reqDTO));
    }

    @PutMapping("/edit")
    @Operation(summary = "修改员工档案", description = "若修改为离职状态，前端需做预警提示")
    @Loggable(title = "员工档案-修改", logRequest = true)
    public ApiResult<Boolean> editEmployee(@Validated @RequestBody EmployeeEditReqDTO reqDTO) {
        return ApiResult.successResult(employeeService.editEmployee(reqDTO));
    }

    @GetMapping("/detail/{id}")
    @Operation(summary = "获取员工详情")
    public ApiResult<EmployeeVO> getDetail(@Parameter(description = "员工ID") @PathVariable Long id) {
        return ApiResult.successResult(employeeService.getEmployeeDetail(id));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除员工档案", description = "默认逻辑删除，物理删除需极高权限")
    @Loggable(title = "员工档案-删除")
    public ApiResult<Boolean> deleteEmployee(
            @Parameter(description = "员工ID") @PathVariable Long id,
            @Parameter(description = "是否逻辑删除") @RequestParam(defaultValue = "true") boolean logicalDelete) {
        return ApiResult.successResult(employeeService.deleteEmployeeById(id, logicalDelete));
    }

    @PostMapping("/deleteBatch")
    @Operation(summary = "批量删除员工档案")
    @Loggable(title = "员工档案-批量删除")
    public ApiResult<Boolean> deleteEmployeeBatch(
            @RequestBody List<Long> ids,
            @RequestParam(defaultValue = "true") boolean logicalDelete) {
        return ApiResult.successResult(employeeService.deleteEmployeeByIds(ids, logicalDelete));
    }

    @GetMapping("/listOption")
    @Operation(summary = "获取在职员工简易列表", description = "用于下拉选择框 (仅限在职员工)")
    public ApiResult<List<EmployeeOptionVO>> listOptions(
            @Parameter(description = "姓名或工号关键字") @RequestParam(required = false) String keyword) {
        return ApiResult.successResult(employeeService.listOption(keyword));
    }
}