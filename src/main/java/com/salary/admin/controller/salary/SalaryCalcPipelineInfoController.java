package com.salary.admin.controller.salary;

import com.salary.admin.annotation.Loggable;
import com.salary.admin.common.ApiResult;
import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.salary.calcpipelineinfo.CalcPipelineInfoAddReqDTO;
import com.salary.admin.model.dto.salary.calcpipelineinfo.CalcPipelineInfoEditReqDTO;
import com.salary.admin.model.dto.salary.calcpipelineinfo.CalcPipelineInfoQueryReqDTO;
import com.salary.admin.model.vo.calcpipelineinfo.CalcPipelineInfoVO;
import com.salary.admin.service.salary.ISalaryCalcPipelineInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 薪资计算管道主表 前端控制器
 * </p>
 *
 * 管道元信息：编码、名称、版本、是否默认、状态等
 * 用于管理不同版本的薪资计算流程
 *
 * @author system
 * @since 2026-03-27
 */
@RestController
@RequestMapping("/api/salary/salary-calc-pipeline-info")
@RequiredArgsConstructor
@Tag(name = "薪资引擎-管道管理 (Pipeline Info)")
public class SalaryCalcPipelineInfoController {

    private final ISalaryCalcPipelineInfoService iSalaryCalcPipelineInfoService;
    // ======================== 1. 新增操作 (Add) ========================
    @PostMapping("/add")
    @Operation(summary = "新增管道配置")
    @Loggable(title = "薪资管道-新增", logRequest = true)
    public ApiResult<Boolean> addPipeline(@Validated @RequestBody CalcPipelineInfoAddReqDTO reqDTO) {
        return ApiResult.successResult(iSalaryCalcPipelineInfoService.addPipeline(reqDTO));
    }
    // ======================== 2. 删除操作 (Delete) ========================
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除管道配置", description = "逻辑删除")
    @Loggable(title = "薪资管道-删除")
    public ApiResult<Boolean> deletePipeline(@Parameter(description = "管道主键ID") @PathVariable Long id) {
        return ApiResult.successResult(iSalaryCalcPipelineInfoService.removeById(id));
    }

    @DeleteMapping("/delete/batch")
    @Operation(summary = "批量删除管道配置", description = "逻辑删除")
    @Loggable(title = "薪资管道-批量删除")
    public ApiResult<Boolean> deletePipelineBatch(@RequestBody List<Long> ids) {
        return ApiResult.successResult(iSalaryCalcPipelineInfoService.removeByIds(ids));
    }
    // ======================== 3. 修改操作 (Update) ========================
    @PutMapping("/edit")
    @Operation(summary = "修改管道配置")
    @Loggable(title = "薪资管道-修改", logRequest = true)
    public ApiResult<Boolean> editPipeline(@Validated @RequestBody CalcPipelineInfoEditReqDTO reqDTO) {
        return ApiResult.successResult(iSalaryCalcPipelineInfoService.updatePipeline(reqDTO));
    }

    @PutMapping("/setDefault/{id}")
    @Operation(summary = "设置默认核算管道", description = "全局唯一默认，将替换原有默认管道")
    @Loggable(title = "薪资管道-设为默认", logRequest = true)
    public ApiResult<Boolean> setDefaultPipeline(@Parameter(description = "管道主键ID") @PathVariable Long id) {
        return ApiResult.successResult(iSalaryCalcPipelineInfoService.setDefaultPipeline(id));
    }
    @PostMapping("/upgradeVersion/{id}")
    @Operation(summary = "复制并升级版本", description = "全量克隆源管道及其所有执行步骤，生成新版本号(如 V1 升 V2)")
    @Loggable(title = "薪资管道-升级版本", logRequest = true)
    public ApiResult<Long> copyAndUpgrade(@Parameter(description = "源管道主键ID") @PathVariable Long id) {
        return ApiResult.successResult(iSalaryCalcPipelineInfoService.copyAndUpgradePipeline(id));
    }
    // ======================== 4. 查询操作 (Query) ========================
    @PostMapping("/page")
    @Operation(summary = "分页查询管道列表")
    public ApiResult<PageResult<CalcPipelineInfoVO>> getPage(@RequestBody CalcPipelineInfoQueryReqDTO reqDTO) {
        return ApiResult.successResult(iSalaryCalcPipelineInfoService.pagePipelineInfo(reqDTO));
    }


}
