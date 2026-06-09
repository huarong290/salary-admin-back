package com.salary.admin.model.dto.salary.calccontext;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 薪资计算上下文快照修改请求参数
 */
@Data
@Schema(description = "薪资计算上下文快照修改请求参数")
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class CalcContextEditReqDTO {

    @Schema(description = "快照主键ID")
    private Long id;

    @Schema(description = "上下文变量JSON字符串")
    private String envJson;

    @Schema(description = "版本号")
    private Integer version;
    /**
     *  备注说明
     */
    @Schema(description = "备注说明")
    private String remark;
}
