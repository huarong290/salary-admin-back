package com.salary.admin.model.dto.salary.calcpipelineinfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 薪资计算管道主表修改请求参数
 *
 * 用于修改管道元信息
 */
@Data
@Schema(description = "薪资计算管道主表修改请求参数")
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class CalcPipelineInfoEditReqDTO {

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 管道名称 */
    @Schema(description = "管道名称")
    private String pipelineName;

    /** 是否默认流程 (1默认 0否) */
    @Schema(description = "是否默认流程 (1默认 0否)")
    private Integer defaultFlag;

    /** 状态 (1启用 0停用) */
    @Schema(description = "状态 (1启用 0停用)")
    private Integer status;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}
