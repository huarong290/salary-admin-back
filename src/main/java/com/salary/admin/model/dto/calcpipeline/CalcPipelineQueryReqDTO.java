package com.salary.admin.model.dto.calcpipeline;

import com.baomidou.mybatisplus.annotation.TableField;
import com.salary.admin.model.dto.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 薪资计算流程管道查询请求参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "薪资计算流程管道查询请求参数")
public class CalcPipelineQueryReqDTO extends PageQueryDTO {

    @Schema(description = "流程编码/名称关键字")
    private String keyword;
    /**
     * 流程编码（如：DEFAULT_PIPELINE）
     */
    @Schema(description = "流程编码（如：DEFAULT_PIPELINE）")
    @TableField("pipeline_code")
    private String pipelineCode;

    @Schema(description = "阶段 (1基础 2补贴 3扣款 4税 5汇总)")
    private Integer stage;

    @Schema(description = "状态 (1启用, 0停用)")
    private Integer status;
}
