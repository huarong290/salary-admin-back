package com.salary.admin.model.dto.salary.calcpipelinestep;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 薪资计算管道步骤-批量保存明细项 DTO
 * (不包含 pipelineCode 和 version，由外层统一赋予，防止数据伪造)
 */
@Data
@Schema(description = "薪资计算管道步骤批量保存明细项")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CalcPipelineStepBatchItemDTO {

    @Schema(description = "规则编码")
    @NotBlank(message = "规则编码不能为空")
    private String ruleCode;

    @Schema(description = "规则名称快照")
    private String ruleName;

    @Schema(description = "规则类型快照 (1公式 2函数)")
    private Integer ruleType;

    @Schema(description = "执行条件表达式")
    private String conditionScript;

    @Schema(description = "阶段 (1基础 2补贴 3扣款 4税 5汇总)")
    @NotNull(message = "执行阶段不能为空")
    private Integer stage;

    @Schema(description = "执行顺序")
    @NotNull(message = "执行顺序不能为空")
    private Integer sortOrder;

    @Schema(description = "失败是否阻断 (1阻断 0不中断)")
    private Integer blockFlag;

    @Schema(description = "结果为空是否跳过 (1跳过 0不跳过)")
    private Integer skipIfNull;

    @Schema(description = "状态 (1启用 0停用)")
    private Integer status;
}
