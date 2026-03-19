package com.salary.admin.controller.salary;

import com.salary.admin.annotation.Loggable;
import com.salary.admin.common.ApiResult;
import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.salary.imcomedetail.IncomeDetailAddReqDTO;
import com.salary.admin.model.dto.salary.imcomedetail.IncomeDetailQueryReqDTO;
import com.salary.admin.model.dto.salary.imcomedetail.IncomeDetailUpdateReqDTO;
import com.salary.admin.model.vo.salary.incomedetail.IncomeDetailVO;
import com.salary.admin.service.salary.ISalaryIncomeDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
@RequiredArgsConstructor
@Tag(name = "薪资流水-当月变动收入明细")
public class SalaryIncomeDetailController {

    private final ISalaryIncomeDetailService iSalaryIncomeDetailService;

    @PostMapping("/add")
    @Operation(summary = "新增单条收入流水明细")
    @Loggable(title = "收入明细-单条新增")
    public ApiResult<Long> add(@Validated @RequestBody IncomeDetailAddReqDTO reqDTO) {
        return ApiResult.successResult(iSalaryIncomeDetailService.addIncomeDetail(reqDTO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改收入流水明细", description = "用于HR发现录入错误时的金额或备注微调")
    @Loggable(title = "收入明细-修改")
    public ApiResult<Boolean> update(@Validated @RequestBody IncomeDetailUpdateReqDTO reqDTO) {
        // 假设你的 Service 层有一个 update 方法，需要传入带 ID 的 DTO
        return ApiResult.successResult(iSalaryIncomeDetailService.updateIncomeDetail(reqDTO));
    }
    @PostMapping("/page")
    @Operation(summary = "分页查询收入流水")
    public ApiResult<PageResult<IncomeDetailVO>> page(@RequestBody IncomeDetailQueryReqDTO reqDTO) {
        return ApiResult.successResult(iSalaryIncomeDetailService.selectIncomeDetailByPage(reqDTO));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除单条收入流水")
    @Loggable(title = "收入明细-单条删除")
    public ApiResult<Boolean> delete(
            @PathVariable("id") Long id,
            @Parameter(description = "是否逻辑删除") @RequestParam(defaultValue = "true") boolean logicalDelete) {
        return ApiResult.successResult(iSalaryIncomeDetailService.deleteById(id, logicalDelete));
    }
    @DeleteMapping("/delete/batch")
    @Operation(summary = "批量删除收入流水", description = "用于清空错误导入的批量数据")
    @Loggable(title = "收入明细-批量删除")
    public ApiResult<Boolean> deleteBatch(
            @RequestBody List<Long> ids,
            @Parameter(description = "是否逻辑删除") @RequestParam(defaultValue = "true") boolean logicalDelete) {
        return ApiResult.successResult(iSalaryIncomeDetailService.deleteByIds(ids, logicalDelete));
    }
    // (Edit 和 Detail 如果需要的话，你可以在接口中通过 updateById 直接扩展，此处保持核心流水能力)
}
