package com.salary.admin.controller;

import com.salary.admin.common.ApiResult;
import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.adjustment.AdjustmentAddReqDTO;
import com.salary.admin.model.dto.adjustment.AdjustmentEditReqDTO;
import com.salary.admin.model.dto.adjustment.AdjustmentQueryDTO;
import com.salary.admin.model.entity.salary.SalaryAdjustment;
import com.salary.admin.service.ISalaryAdjustmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 薪资周期专项调整表 (处理各类动态奖金与扣款) 前端控制器
 * </p>
 *
 * @author system
 * @since 2026-04-05
 */
@Tag(name = "专项调整(手工账/动态发薪)接口")
@RestController
@RequestMapping("/api/salary/adjustment")
@RequiredArgsConstructor
public class SalaryAdjustmentController {

    private final ISalaryAdjustmentService iSalaryAdjustmentService;

    @Operation(summary = "1. 新增专项调整")
    @PostMapping("/add")
    public ApiResult<Long> add(@Validated @RequestBody AdjustmentAddReqDTO reqDTO) {
        return ApiResult.successResult(iSalaryAdjustmentService.addAdjustment(reqDTO));
    }

    @Operation(summary = "2. 修改专项调整 (仅限草稿状态)")
    @PutMapping("/edit")
    public ApiResult<Boolean> edit(@Validated @RequestBody AdjustmentEditReqDTO reqDTO) {
        return ApiResult.successResult(iSalaryAdjustmentService.editAdjustment(reqDTO));
    }

    @Operation(summary = "3. 批量删除专项调整 (仅限草稿状态)")
    @DeleteMapping("/delete")
    public ApiResult<Boolean> delete(@RequestBody List<Long> ids) {
        return ApiResult.successResult(iSalaryAdjustmentService.deleteAdjustments(ids));
    }

    @Operation(summary = "4. 批量生效/撤回 (0-草稿, 1-已生效)")
    @PutMapping("/audit/{status}")
    public ApiResult<Boolean> audit(@RequestBody List<Long> ids, @PathVariable("status") Integer status) {
        return ApiResult.successResult(iSalaryAdjustmentService.auditAdjustments(ids, status));
    }

    @Operation(summary = "5. 分页查询专项调整列表")
    @GetMapping("/page")
    public ApiResult<PageResult<SalaryAdjustment>> page(@Validated AdjustmentQueryDTO queryDTO) {
        // 使用 ApiResult 统一包装，适配前端 axios 拦截器的解包逻辑
        return ApiResult.successResult(iSalaryAdjustmentService.pageQuery(queryDTO));
    }
}