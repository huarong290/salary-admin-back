package com.salary.admin.controller.salary;

import com.salary.admin.annotation.Loggable;
import com.salary.admin.common.ApiResult;
import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.salary.imcometype.IncomeTypeAddReqDTO;
import com.salary.admin.model.dto.salary.imcometype.IncomeTypeEditReqDTO;
import com.salary.admin.model.dto.salary.imcometype.IncomeTypeQueryReqDTO;
import com.salary.admin.model.vo.salary.incometype.IncomeTypeVO;
import com.salary.admin.service.salary.ISalaryIncomeTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 收入类型字典表 前端控制器
 * </p>
 *
 * @author system
 * @since 2026-03-11
 */
@RestController
@RequestMapping("/api/salary/income-type")
@Tag(name = "薪资配置-收入类型字典")
public class SalaryIncomeTypeController {

    @Autowired
    private ISalaryIncomeTypeService iSalaryIncomeTypeService;

    @PostMapping("/add")
    @Operation(summary = "新增收入类型")
    @Loggable(title = "收入类型-新增")
    public ApiResult<Long> add(@Validated @RequestBody IncomeTypeAddReqDTO reqDTO) {
        return ApiResult.successResult(iSalaryIncomeTypeService.addIncomeType(reqDTO));
    }

    @PutMapping("/edit")
    @Operation(summary = "修改收入类型")
    @Loggable(title = "收入类型-修改")
    public ApiResult<Boolean> edit(@Validated @RequestBody IncomeTypeEditReqDTO reqDTO) {
        return ApiResult.successResult(iSalaryIncomeTypeService.editIncomeType(reqDTO));
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询收入类型")
    public ApiResult<PageResult<IncomeTypeVO>> page(@RequestBody IncomeTypeQueryReqDTO reqDTO) {
        return ApiResult.successResult(iSalaryIncomeTypeService.selectIncomeTypePage(reqDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取收入类型详情")
    public ApiResult<IncomeTypeVO> detail(@PathVariable("id") Long id) {
        return ApiResult.successResult(iSalaryIncomeTypeService.getIncomeTypeDetail(id));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除收入类型")
    @Loggable(title = "收入类型-单条删除")
    public ApiResult<Boolean> delete(
            @PathVariable("id") Long id,
            @Parameter(description = "是否逻辑删除") @RequestParam(defaultValue = "true") boolean logicalDelete) {
        return ApiResult.successResult(iSalaryIncomeTypeService.deleteById(id, logicalDelete));
    }

    @DeleteMapping("/delete/batch")
    @Operation(summary = "批量删除收入类型")
    @Loggable(title = "收入类型-批量删除")
    public ApiResult<Boolean> deleteBatch(
            @RequestBody List<Long> ids,
            @Parameter(description = "是否逻辑删除") @RequestParam(defaultValue = "true") boolean logicalDelete) {
        return ApiResult.successResult(iSalaryIncomeTypeService.deleteByIds(ids, logicalDelete));
    }
}