package com.salary.admin.model.dto.salary.period;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 新增薪资周期 DTO
 */
@Data
@Schema(description = "新增薪资周期请求")
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
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
    private BigDecimal monthDays;
    /**
     * 出勤天数
     */
    @Schema(description = "出勤天数")
    private BigDecimal attendanceDays;
    /**
     * 现场出勤天数
     */
    @Schema(description = "现场出勤天数")
    private BigDecimal officeDays;
    /**
     * 居家出勤天数
     */
    @Schema(description = "居家出勤天数")
    private BigDecimal wfhDays;
    /**
     * 非带薪假/欠勤天数
     */
    @Schema(description = "非带薪假/欠勤天数")
    private BigDecimal unpaidLeaveDays;
    /**
     * 带薪假天数(如年假、调休)
     */
    @Schema(description = "带薪假天数(如年假、调休)")
    private BigDecimal paidLeaveDays;

    /**
     * 是否满勤 (1:是, 0:否)
     */
    @Schema(description = "是否满勤 (1:是, 0:否)")
    private Integer fullAttendanceFlag;
    /**
     * 累计在岗月份计数 (入职首月为1, 递增)
     */
    @Schema(description = "累计在岗月份计数 (入职首月为1, 递增)")
    private Integer workMonth;
}


