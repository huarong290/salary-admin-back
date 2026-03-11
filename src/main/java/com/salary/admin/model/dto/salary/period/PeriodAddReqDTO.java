package com.salary.admin.model.dto.salary.period;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 新增薪资周期 DTO
 */
@Data
@Schema(description = "新增薪资周期请求")
public class PeriodAddReqDTO implements Serializable {
    /**
     * 员工ID
     */
    @NotNull(message = "员工ID不能为空")
    @Schema(description = "员工ID")
    private Long employeeId;
    /**
     * 结算月份
     */
    @NotBlank(message = "结算月份不能为空")
    @Schema(description = "结算月份(格式：YYYYMM)")
    private String settlementMonth;
    /**
     * 开始日期
     */
    @Schema(description = "开始日期")
    private LocalDate startDate;
    /**
     * 结束日期
     */
    @Schema(description = "结束日期")
    private LocalDate endDate;

    /**
     * 月天数
     */
    @Schema(description = "月天数")
    private Integer monthDays;
    /**
     * 出勤天数
     */
    @Schema(description = "出勤天数")
    private Integer attendanceDays;
}


