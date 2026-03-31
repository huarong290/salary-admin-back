package com.salary.admin.model.dto.calccontext;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 薪资计算上下文快照修改请求参数
 */
@Data
@Schema(description = "薪资计算上下文快照修改请求参数")
public class CalcContextEditReqDTO {

    @Schema(description = "快照主键ID")
    private Long id;

    @Schema(description = "上下文变量JSON字符串")
    private String contextJson;

    @Schema(description = "版本号")
    private Integer version;
}
