package com.salary.admin.controller.salary;

import com.salary.admin.common.ApiResult;
import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.salary.calcrule.CalcRuleAddReqDTO;
import com.salary.admin.model.dto.salary.calcrule.CalcRuleEditReqDTO;
import com.salary.admin.model.dto.salary.calcrule.CalcRuleQueryReqDTO;
import com.salary.admin.model.vo.calcrule.CalcRuleVO;
import com.salary.admin.service.salary.ISalaryCalcRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 薪资计算规则库表 前端控制器
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
@Tag(name = "薪资计算规则", description = "计算引擎规则库维护")
@RestController
@RequestMapping("/api/salary/calc-rule")
@RequiredArgsConstructor
public class SalaryCalcRuleController {

    private final ISalaryCalcRuleService ruleService;

    @Operation(summary = "分页查询规则列表")
    @PostMapping("/page")
    public ApiResult<PageResult<CalcRuleVO>> getPage(@RequestBody CalcRuleQueryReqDTO reqDTO) {
        return ApiResult.successResult(ruleService.getCalcRulePage(reqDTO));
    }

    @Operation(summary = "新增计算规则")
    @PostMapping("/add")
    public ApiResult<Long> add(@RequestBody @Validated CalcRuleAddReqDTO reqDTO) {
        return ApiResult.successResult(ruleService.addRule(reqDTO));
    }

    @Operation(summary = "修改计算规则")
    @PutMapping("/edit")
    public ApiResult<Boolean> edit(@RequestBody @Validated CalcRuleEditReqDTO reqDTO) {
        return ApiResult.successResult(ruleService.editRule(reqDTO));
    }

    @Operation(summary = "删除规则 (支持逻辑/物理)")
    @DeleteMapping("/{id}")
    public ApiResult<Boolean> delete(@PathVariable Long id, @RequestParam(defaultValue = "true") boolean logical) {
        return ApiResult.successResult(ruleService.deleteRuleById(id, logical));
    }
}