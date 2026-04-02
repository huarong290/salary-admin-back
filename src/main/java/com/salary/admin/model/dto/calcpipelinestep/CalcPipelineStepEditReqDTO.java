package com.salary.admin.model.dto.calcpipelinestep;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 薪资计算管道步骤修改请求参数
 *
 * 用于修改管道执行步骤
 */
@Data
@Schema(description = "薪资计算管道步骤修改请求参数")
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class CalcPipelineStepEditReqDTO {

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 执行条件表达式 */
    @Schema(description = "执行条件表达式")
    private String conditionScript;

    /** 执行顺序 */
    @Schema(description = "执行顺序")
    private Integer sortOrder;

    /** 失败是否阻断 (1阻断 0不中断) */
    @Schema(description = "失败是否阻断 (1阻断 0不中断)")
    private Integer blockFlag;

    /** 结果为空是否跳过 (1跳过 0不跳过) */
    @Schema(description = "结果为空是否跳过 (1跳过 0不跳过)")
    private Integer skipIfNull;

    /** 状态 (1启用 0停用) */
    @Schema(description = "状态 (1启用 0停用)")
    private Integer status;
}

