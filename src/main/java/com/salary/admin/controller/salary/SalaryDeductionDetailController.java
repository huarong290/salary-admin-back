package com.salary.admin.controller.salary;

import com.salary.admin.annotation.Loggable;
import com.salary.admin.common.ApiResult;
import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.salary.deductiondetail.DeductionDetailAddReqDTO;
import com.salary.admin.model.dto.salary.deductiondetail.DeductionDetailQueryReqDTO;
import com.salary.admin.model.vo.deductiondetail.DeductionDetailVO;
import com.salary.admin.service.salary.ISalaryDeductionDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 员工扣款明细表 前端控制器
 * </p>
 *
 * @author system
 * @since 2026-03-11
 */
@RestController
@RequestMapping("/api/salary/deduction-detail")
@Tag(name = "薪资流水-扣款明细")
public class SalaryDeductionDetailController {

    @Autowired
    private ISalaryDeductionDetailService iSalaryDeductionDetailService;

    @PostMapping("/add")
    @Operation(summary = "新增扣款流水明细")
    @Loggable(title = "扣款明细-新增")
    public ApiResult<Long> add(@Validated @RequestBody DeductionDetailAddReqDTO reqDTO) {
        return ApiResult.successResult(iSalaryDeductionDetailService.addDeductionDetail(reqDTO));
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询扣款流水")
    public ApiResult<PageResult<DeductionDetailVO>> page(@RequestBody DeductionDetailQueryReqDTO reqDTO) {
        return ApiResult.successResult(iSalaryDeductionDetailService.selectDeductionDetailPage(reqDTO));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除单条扣款流水")
    @Loggable(title = "扣款明细-单条删除")
    public ApiResult<Boolean> delete(
            @PathVariable("id") Long id,
            @Parameter(description = "是否逻辑删除") @RequestParam(defaultValue = "true") boolean logicalDelete) {
        return ApiResult.successResult(iSalaryDeductionDetailService.deleteById(id, logicalDelete));
    }
}