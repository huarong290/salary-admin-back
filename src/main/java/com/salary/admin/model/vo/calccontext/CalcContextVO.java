package com.salary.admin.model.vo.calccontext;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 薪资计算上下文快照视图对象
 */
@Data
@Schema(description = "薪资计算上下文快照视图对象")
public class CalcContextVO {

    @Schema(description = "快照主键ID")
    private Long id;

    @Schema(description = "员工ID")
    private Long employeeId;

    @Schema(description = "薪资周期ID")
    private Long periodId;

    @Schema(description = "上下文变量JSON字符串")
    private String contextJson;

    @Schema(description = "使用的流程编码")
    private String pipelineCode;

    @Schema(description = "版本号")
    private Integer version;
}

