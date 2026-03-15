package com.salary.admin.controller.salary;

import com.salary.admin.annotation.Loggable;
import com.salary.admin.common.ApiResult;
import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.salary.deductiontype.DeductionTypeAddReqDTO;
import com.salary.admin.model.dto.salary.deductiontype.DeductionTypeEditReqDTO;
import com.salary.admin.model.dto.salary.deductiontype.DeductionTypeQueryReqDTO;
import com.salary.admin.model.vo.salary.deductiontype.DeductionTypeOptionVO;
import com.salary.admin.model.vo.salary.deductiontype.DeductionTypeVO;
import com.salary.admin.service.salary.ISalaryDeductionTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 扣款类型字典表 前端控制器
 * </p>
 *
 * @author system
 * @since 2026-03-11
 */
@RestController
@RequestMapping("/api/salary/deduction-type")
@Tag(name = "薪资配置-扣款类型字典")
public class SalaryDeductionTypeController {

    @Autowired
    private ISalaryDeductionTypeService iSalaryDeductionTypeService;

    @PostMapping("/add")
    @Operation(summary = "新增扣款类型")
    @Loggable(title = "扣款类型-新增")
    public ApiResult<Long> add(@Validated @RequestBody DeductionTypeAddReqDTO reqDTO) {
        return ApiResult.successResult(iSalaryDeductionTypeService.addDeductionType(reqDTO));
    }

    @PutMapping("/edit")
    @Operation(summary = "修改扣款类型")
    @Loggable(title = "扣款类型-修改")
    public ApiResult<Boolean> edit(@Validated @RequestBody DeductionTypeEditReqDTO reqDTO) {
        return ApiResult.successResult(iSalaryDeductionTypeService.editDeductionType(reqDTO));
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询扣款类型")
    public ApiResult<PageResult<DeductionTypeVO>> page(@RequestBody DeductionTypeQueryReqDTO reqDTO) {
        return ApiResult.successResult(iSalaryDeductionTypeService.selectDeductionTypePage(reqDTO));
    }

    @GetMapping("/{id:\\d+}")
    @Operation(summary = "获取扣款类型详情")
    public ApiResult<DeductionTypeVO> detail(@PathVariable("id") Long id) {
        return ApiResult.successResult(iSalaryDeductionTypeService.getDeductionTypeDetail(id));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除扣款类型")
    @Loggable(title = "扣款类型-单条删除")
    public ApiResult<Boolean> delete(
            @PathVariable("id") Long id,
            @Parameter(description = "是否逻辑删除") @RequestParam(defaultValue = "true") boolean logicalDelete) {
        return ApiResult.successResult(iSalaryDeductionTypeService.deleteById(id, logicalDelete));
    }

    @DeleteMapping("/delete/batch")
    @Operation(summary = "批量删除扣款类型")
    @Loggable(title = "扣款类型-批量删除")
    public ApiResult<Boolean> deleteBatch(
            @RequestBody List<Long> ids,
            @Parameter(description = "是否逻辑删除") @RequestParam(defaultValue = "true") boolean logicalDelete) {
        return ApiResult.successResult(iSalaryDeductionTypeService.deleteByIds(ids, logicalDelete));
    }

    @GetMapping("/listOptions")
    @Operation(summary = "获取扣款类型下拉列表")
    public ApiResult<List<DeductionTypeOptionVO>> listOptions() {
        return ApiResult.successResult(iSalaryDeductionTypeService.listDeductionTypeOptions());
    }
}
