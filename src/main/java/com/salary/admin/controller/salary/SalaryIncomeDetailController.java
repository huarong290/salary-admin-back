package com.salary.admin.controller.salary;

import com.salary.admin.annotation.Loggable;
import com.salary.admin.common.ApiResult;
import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.salary.imcomedetail.IncomeDetailAddReqDTO;
import com.salary.admin.model.dto.salary.imcomedetail.IncomeDetailQueryReqDTO;
import com.salary.admin.model.vo.salary.incomedetail.IncomeDetailVO;
import com.salary.admin.service.salary.ISalaryIncomeDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 员工收入明细表 前端控制器
 * </p>
 *
 * @author system
 * @since 2026-03-11
 */
@RestController
@RequestMapping("/api/salary/income-detail")
@Tag(name = "薪资流水-收入明细")
public class SalaryIncomeDetailController {

    @Autowired
    private ISalaryIncomeDetailService iSalaryIncomeDetailService;

    @PostMapping("/add")
    @Operation(summary = "新增收入流水明细")
    @Loggable(title = "收入明细-新增")
    public ApiResult<Long> add(@Validated @RequestBody IncomeDetailAddReqDTO reqDTO) {
        return ApiResult.successResult(iSalaryIncomeDetailService.addIncomeDetail(reqDTO));
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询收入流水")
    public ApiResult<PageResult<IncomeDetailVO>> page(@RequestBody IncomeDetailQueryReqDTO reqDTO) {
        return ApiResult.successResult(iSalaryIncomeDetailService.selectIncomeDetailPage(reqDTO));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除单条收入流水")
    @Loggable(title = "收入明细-单条删除")
    public ApiResult<Boolean> delete(
            @PathVariable("id") Long id,
            @Parameter(description = "是否逻辑删除") @RequestParam(defaultValue = "true") boolean logicalDelete) {
        return ApiResult.successResult(iSalaryIncomeDetailService.deleteById(id, logicalDelete));
    }

    // (Edit 和 Detail 如果需要的话，你可以在接口中通过 updateById 直接扩展，此处保持核心流水能力)
}
