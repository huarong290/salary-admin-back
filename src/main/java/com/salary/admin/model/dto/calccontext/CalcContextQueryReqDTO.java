package com.salary.admin.model.dto.calccontext;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 * 薪资计算上下文快照查询请求参数
 */
@Data
@Schema(description = "薪资计算上下文快照查询请求参数")
public class CalcContextQueryReqDTO {

    @Schema(description = "员工ID")
    private Long employeeId;

    @Schema(description = "薪资周期ID")
    private Long periodId;

    @Schema(description = "版本号")
    private Integer version;
}
