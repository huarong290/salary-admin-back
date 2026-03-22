package com.salary.admin.model.dto.salary.period;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "批量初始化薪资周期请求")
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class PeriodBatchInitReqDTO implements Serializable {
    /**
     * 结算月份(YYYYMM)
     */
    @NotBlank(message = "结算月份不能为空")
    @Schema(description = "结算月份(YYYYMM)")
    private String settlementMonth;
    /**
     * 指定员工ID列表(为空则默认为所有在职员工)
     */
    @Schema(description = "指定员工ID列表(为空则默认为所有在职员工)")
    private List<Long> employeeIds;
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
     * 是否满勤 (1:是, 0:否)
     */
    @Schema(description = "是否满勤 (1:是, 0:否)")
    private Integer fullAttendanceFlag;
    /**
     *附加备注
     */
    private String remark;
}
