package com.salary.admin.controller.salary;

import com.salary.admin.common.ApiResult;
import com.salary.admin.model.dto.calcpipelinestep.CalcPipelineStepAddReqDTO;
import com.salary.admin.model.vo.calcpipelinestep.CalcPipelineStepVO;
import com.salary.admin.service.salary.ISalaryCalcPipelineStepService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 薪资计算管道步骤表 前端控制器
 * </p>
 *
 * 管道执行步骤：规则快照、阶段、顺序、执行控制等
 * 支持条件表达式、阻断策略、跳过策略等灵活配置
 *
 * @author system
 * @since 2026-03-27
 */
@RestController
@RequestMapping("/api/salary/salary-calc-pipeline-step")
@RequiredArgsConstructor
@Tag(name = "薪资引擎-管道步骤管理 (Pipeline Step)")
public class SalaryCalcPipelineStepController {
    private final ISalaryCalcPipelineStepService iSalaryCalcPipelineStepService;
    // ======================== 1. 查询操作 (Read) ========================

    // ======================== 2. 删除操作 (Delete) ========================

    // ======================== 3. 修改操作 (Update) ========================
    @Operation(summary = "批量保存管道步骤配置 (全量覆盖)")
    @PostMapping("/batch-save")
    public ApiResult<Boolean> batchSaveSteps(@RequestParam String pipelineCode,
                                             @RequestParam Integer pipelineVersion,
                                             @RequestBody @Validated List<CalcPipelineStepAddReqDTO> stepList) {
        boolean success = iSalaryCalcPipelineStepService.batchSaveSteps(pipelineCode, pipelineVersion, stepList);
        return ApiResult.successResult(success);
    }
    // ======================== 4. 查询操作 (Query) ========================
    @Operation(summary = "获取管道的所有执行步骤")
    @GetMapping("/list")
    public ApiResult<List<CalcPipelineStepVO>> listSteps(@RequestParam String pipelineCode,
                                                         @RequestParam Integer pipelineVersion) {
        List<CalcPipelineStepVO> result = iSalaryCalcPipelineStepService.listSteps(pipelineCode, pipelineVersion);
        return ApiResult.successResult(result);
    }
}
