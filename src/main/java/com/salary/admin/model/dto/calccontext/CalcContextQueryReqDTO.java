package com.salary.admin.model.dto.calccontext;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 * 薪资计算上下文快照查询请求参数
 */
@Data
@Schema(description = "薪资计算上下文快照查询请求参数")
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class CalcContextQueryReqDTO {

    @Schema(description = "员工ID")
    private Long employeeId;

    @Schema(description = "薪资周期ID")
    private Long periodId;

    @Schema(description = "版本号")
    private Integer version;
    /**
     *  备注说明
     */
    @Schema(description = "备注说明")
    private String remark;
}
