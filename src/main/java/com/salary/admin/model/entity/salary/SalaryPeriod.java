package com.salary.admin.model.entity.salary;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.salary.admin.model.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 薪资周期信息表
 *
 * @author system
 * @since 2026-03-27
 */
@Schema(name = "SalaryPeriod", description = "薪资周期信息表")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("salary_period")
public class SalaryPeriod extends BaseEntity<SalaryPeriod> {

    private static final long serialVersionUID = 1L;

    /**
     * 周期ID
     */
    @Schema(description = "周期ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 员工ID
     */
    @Schema(description = "员工ID")
    @TableField("employee_id")
    private Long employeeId;
    /**
     * 累计在岗月份计数 (入职首月为1, 递增)
     */
    @Schema(description = "累计在岗月份计数 (入职首月为1, 递增))")
    @TableField("work_month")
    private Integer workMonth;
    /**
     * 结算月份 (YYYYMM)
     */
    @Schema(description = "结算月份 (YYYYMM)")
    @TableField("settlement_month")
    private String settlementMonth;
    /**
     * 开始日期
     */
    @Schema(description = "开始日期")
    @TableField("start_date")
    private LocalDate startDate;
    /**
     * 结束日期
     */
    @Schema(description = "结束日期")
    @TableField("end_date")
    private LocalDate endDate;
    /**
     * 月天数
     */
    @Schema(description = "月天数")
    @TableField("month_days")
    private BigDecimal monthDays;
    /**
     * 出勤天数
     */
    @Schema(description = "出勤天数")
    @TableField("attendance_days")
    private BigDecimal attendanceDays;

    /**
     * 现场出勤天数
     */
    @Schema(description = "现场出勤天数")
    @TableField("office_days")
    private BigDecimal officeDays;
    /**
     * 居家出勤天数
     */
    @Schema(description = "居家出勤天数")
    @TableField("wfh_days")
    private BigDecimal wfhDays;

    /**
     * 非带薪假/欠勤天数
     */
    @Schema(description = "非带薪假/欠勤天数")
    @TableField("unpaid_leave_days")
    private BigDecimal unpaidLeaveDays;

    /**
     * 带薪假天数(如年假、调休)
     */
    @Schema(description = "带薪假天数(如年假、调休)")
    @TableField("paid_leave_days")
    private BigDecimal paidLeaveDays;

    /**
     * 是否满勤 (1:是, 0:否)
     */
    @Schema(description = "是否满勤 (1:是, 0:否)")
    @TableField("full_attendance_flag")
    private Integer fullAttendanceFlag;
    /**
     * 删除标识
     */
    @Schema(description = "删除标识")

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}