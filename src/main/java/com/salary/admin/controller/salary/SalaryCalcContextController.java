package com.salary.admin.controller.salary;

import com.salary.admin.common.ApiResult;
import com.salary.admin.model.vo.calccontext.CalcContextVO;
import com.salary.admin.service.salary.ISalaryCalcContextService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 薪资计算上下文快照表 前端控制器
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
@Tag(name = "计算上下文快照", description = "核算时的环境变量全量备份")
@RestController
@RequestMapping("/api/salary/calc-context")
@RequiredArgsConstructor
public class SalaryCalcContextController {

    private final ISalaryCalcContextService contextService;

    @Operation(summary = "查看快照详情", description = "获取当时计算的具体变量环境 JSON")
    @GetMapping("/{id}")
    public ApiResult<CalcContextVO> getDetail(@PathVariable Long id) {
        return ApiResult.successResult(contextService.getContextDetail(id));
    }
}