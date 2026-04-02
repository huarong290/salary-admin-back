package com.salary.admin.model.vo.calccontext;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 薪资计算上下文快照视图对象
 */
@Data
@Schema(description = "薪资计算上下文快照视图对象")
public class CalcContextVO {
    /**
     *  快照主键ID
     */
    @Schema(description = "快照主键ID")
    private Long id;
    /**
     *  员工ID
     */
    @Schema(description = "员工ID")
    private Long employeeId;
    /**
     *  薪资周期ID
     */
    @Schema(description = "薪资周期ID")
    private Long periodId;
    /**
     *  来源档案ID (记录当时计算是基于哪个版本的定薪档案)
     */
    @Schema(description = "来源档案ID")
    private Long archiveId;
    /**
     *  使用的流程编码
     */
    @Schema(description = "上下文变量JSON字符串")
    private String envJson;
    /**
     *  使用的流程编码
     */
    @Schema(description = "使用的流程编码")
    private String pipelineCode;

    /**
     *  使用的流程管道版本号
     */
    @Schema(description = "使用的流程管道版本号")
    private Integer pipelineVersion;
    /**
     *  版本号
     */
    @Schema(description = "版本号")
    private Integer version;

    /**
     *  备注说明
     */
    @Schema(description = "备注说明")
    private String remark;
}

