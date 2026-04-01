package com.salary.admin.model.vo.calcpipelineinfo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 薪资计算管道主表视图对象
 *
 * 用于前端展示管道元信息：编码、名称、版本、是否默认、状态等
 */
@Data
@Schema(description = "薪资计算管道主表视图对象")
public class CalcPipelineInfoVO {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 管道唯一编码
     */
    @Schema(description = "管道唯一编码")
    private String pipelineCode;

    /**
     * 管道名称
     */
    @Schema(description = "管道名称")
    private String pipelineName;

    /**
     * 版本号
     */
    @Schema(description = "版本号")
    private Integer version;

    /**
     * 是否默认流程 (1默认 0否)
     */
    @Schema(description = "是否默认流程 (1默认 0否)")
    private Integer defaultFlag;

    /**
     * 状态 (1启用 0停用)
     */
    @Schema(description = "状态 (1启用 0停用)")
    private Integer status;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
}
