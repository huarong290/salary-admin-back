package com.salary.admin.model.dto.calcpipeline;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;



/**
 * 薪资流程管道-批量保存明细项 DTO
 */
@Data
@Schema(description = "薪资流程管道-批量保存明细项请求参数")
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class CalcPipelineItemDTO {

    @Schema(description = "流程编码（如：DEFAULT_PIPELINE）")
    @NotBlank(message = "流程编码不能为空")
    private String pipelineCode;

    @Schema(description = "流程名称")
    private String pipelineName;

    @Schema(description = "阶段（1基础 2补贴 3扣款 4税 5汇总）")
    @NotNull(message = "执行阶段不能为空")
    private Integer stage;

    @Schema(description = "规则编码（关联 salary_calc_rule）")
    @NotBlank(message = "绑定的规则编码不能为空")
    private String ruleCode;

    @Schema(description = "状态 (1启用 0停用)")
    private Integer status;

    // 💡 注意：这里故意不暴露 id 和 sortOrder 给前端修改，因为全量覆盖模式下，
    // id 应该由数据库重新生成，sortOrder 应该由后端根据数组索引重新强制计算。
}
