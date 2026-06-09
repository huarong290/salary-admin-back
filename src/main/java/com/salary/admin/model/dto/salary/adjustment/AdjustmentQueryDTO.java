package com.salary.admin.model.dto.salary.adjustment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.salary.admin.model.dto.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 薪资专项调整-分页查询参数
 */
@Data
@Schema(description = "薪资专项调整-分页查询请求参数")
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdjustmentQueryDTO extends PageQueryDTO {

    @Schema(description = "关联核算周期ID (精确查询)")
    private Long periodId;

    @Schema(description = "员工ID (精确查询)")
    private Long employeeId;

    @Schema(description = "薪资项编码 (精确查询，如：HOLIDAY_BONUS)")
    private String itemCode;

    @Schema(description = "数据状态: 0-草稿, 1-已生效")
    private Integer status;
}
