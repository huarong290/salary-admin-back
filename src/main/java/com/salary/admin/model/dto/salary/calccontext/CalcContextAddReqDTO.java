package com.salary.admin.model.dto.salary.calccontext;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 薪资计算上下文快照新增请求参数
 */
@Data
@Schema(description = "薪资计算上下文快照新增请求参数")
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class CalcContextAddReqDTO {

    @Schema(description = "员工ID")
    private Long employeeId;

    @Schema(description = "薪资周期ID")
    private Long periodId;
    /**
     *  来源档案ID (记录当时计算是基于哪个版本的定薪档案)
     */
    @Schema(description = "来源档案ID")
    private Long archiveId;
    /**
     *  上下文变量JSON字符串
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
