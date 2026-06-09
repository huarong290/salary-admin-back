package com.salary.admin.model.dto.salary.calcpipelinestep;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.salary.admin.model.dto.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 薪资计算管道步骤查询请求参数
 *
 * 用于分页查询管道执行步骤
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "薪资计算管道步骤查询请求参数")
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class CalcPipelineStepQueryReqDTO extends PageQueryDTO {

    /** 所属管道编码 */
    @Schema(description = "所属管道编码")
    private String pipelineCode;

    /** 管道版本 */
    @Schema(description = "管道版本")
    private Integer pipelineVersion;

    /** 阶段 (1基础 2补贴 3扣款 4税 5汇总) */
    @Schema(description = "阶段 (1基础 2补贴 3扣款 4税 5汇总)")
    private Integer stage;

    /** 状态 (1启用 0停用) */
    @Schema(description = "状态 (1启用 0停用)")
    private Integer status;
}

