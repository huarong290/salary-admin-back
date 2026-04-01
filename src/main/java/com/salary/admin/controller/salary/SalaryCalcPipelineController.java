package com.salary.admin.controller.salary;

import com.salary.admin.common.ApiResult;
import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.calcpipeline.CalcPipelineAddReqDTO;
import com.salary.admin.model.dto.calcpipeline.CalcPipelineEditReqDTO;
import com.salary.admin.model.dto.calcpipeline.CalcPipelineItemDTO;
import com.salary.admin.model.dto.calcpipeline.CalcPipelineQueryReqDTO;
import com.salary.admin.model.vo.calcpipeline.CalcPipelineVO;
import com.salary.admin.service.salary.ISalaryCalcPipelineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 薪资计算流程管道表 前端控制器
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
@Tag(name = "薪资流程管道", description = "维护薪资核算步骤与执行顺序")
@RestController
@RequestMapping("/api/salary/calc-pipeline")
@RequiredArgsConstructor
public class SalaryCalcPipelineController {

    private final ISalaryCalcPipelineService pipelineService;

    // ======================== 1. 查询操作 (Read) ========================

    @Operation(summary = "分页查询流程管道")
    @PostMapping("/page")
    public ApiResult<PageResult<CalcPipelineVO>> getPage(@RequestBody CalcPipelineQueryReqDTO reqDTO) {
        return ApiResult.successResult(pipelineService.getPipelinePage(reqDTO));
    }

    @Operation(summary = "获取管道详情")
    @GetMapping("/{id}")
    public ApiResult<CalcPipelineVO> getDetail(@PathVariable Long id) {
        return ApiResult.successResult(pipelineService.getPipelineDetail(id));
    }

    // ======================== 2. 新增与批量保存 (Create/Save) ========================

    @Operation(summary = "单条新增流程管道")
    @PostMapping("/add")
    public ApiResult<Long> add(@RequestBody @Validated CalcPipelineAddReqDTO reqDTO) {
        return ApiResult.successResult(pipelineService.addPipeline(reqDTO));
    }

    @Operation(summary = "批量保存/重排管道", description = "用于拖拽排序后一键保存全量步骤")
    @PostMapping("/batch-save")
    public ApiResult<Boolean> batchSave(
            @RequestParam String pipelineCode,
            @RequestBody List<CalcPipelineItemDTO> pipelines) {
        return ApiResult.successResult(pipelineService.savePipelineBatchMode(pipelineCode, pipelines));
    }

    // ======================== 3. 修改操作 (Update) ========================

    @Operation(summary = "修改流程管道")
    @PutMapping("/edit")
    public ApiResult<Boolean> edit(@RequestBody @Validated CalcPipelineEditReqDTO reqDTO) {
        return ApiResult.successResult(pipelineService.editPipeline(reqDTO));
    }

    // ======================== 4. 删除操作 (Delete) ========================

    @Operation(summary = "删除单个管道步骤")
    @DeleteMapping("/{id}")
    public ApiResult<Boolean> delete(
            @PathVariable Long id,
            @RequestParam(defaultValue = "true") boolean logical) {
        return ApiResult.successResult(pipelineService.deletePipelineById(id, logical));
    }

    @Operation(summary = "批量删除管道步骤")
    @DeleteMapping("/batch")
    public ApiResult<Boolean> deleteBatch(
            @RequestBody List<Long> ids,
            @RequestParam(defaultValue = "true") boolean logical) {
        return ApiResult.successResult(pipelineService.deletePipelineByIds(ids, logical));
    }
}
