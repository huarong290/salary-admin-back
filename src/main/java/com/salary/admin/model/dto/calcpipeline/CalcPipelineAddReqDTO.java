package com.salary.admin.model.dto.calcpipeline;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 薪资计算流程管道新增请求参数
 */
@Data
@Schema(description = "薪资计算流程管道新增请求参数")
public class CalcPipelineAddReqDTO {

    @Schema(description = "流程编码")
    private String pipelineCode;

    @Schema(description = "流程名称")
    private String pipelineName;

    @Schema(description = "阶段 (1基础 2补贴 3扣款 4税 5汇总)")
    private Integer stage;

    @Schema(description = "规则编码 (关联 salary_calc_rule)")
    private String ruleCode;

    @Schema(description = "执行顺序")
    private Integer sortOrder;

    @Schema(description = "状态 (1启用, 0停用)")
    private Integer status;
}
