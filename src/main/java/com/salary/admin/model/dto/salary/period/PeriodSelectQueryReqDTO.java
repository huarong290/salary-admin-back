package com.salary.admin.model.dto.salary.period;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 查询薪资周期下拉列表请求 DTO
 * * @author system
 * @since 2026-03-11
 */
@Data

@Schema(description = "查询薪资周期下拉列表请求")
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class PeriodSelectQueryReqDTO {
    /**
     * 员工ID
     */
    @Schema(description = "员工ID")
    private Long employeeId;
}
