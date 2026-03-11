package com.salary.admin.model.dto.salary.summary;

import com.salary.admin.model.dto.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 薪资汇总列表查询 DTO
 */
@Data
@Schema(description = "分页查询薪资汇总请求")
public class SummaryQueryReqDTO extends PageQueryDTO {
    @Schema(description = "指定查询的薪资周期ID")
    private Long periodId;

    @Schema(description = "结算月份 (如: 202603)")
    private String settlementMonth;

    @Schema(description = "员工ID (用于查询某员工的历史工资单)")
    private Long employeeId;
}
