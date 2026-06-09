package com.salary.admin.model.vo.salary.period;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 薪资周期返回对象 VO
 * 用于展示员工在特定结算月份的出勤及周期基础信息
 *
 * @author system
 * @since 2026-03-11
 */
@Data
@Schema(description = "薪资周期视图对象")
public class PeriodVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 周期ID
     */
    @Schema(description = "周期ID")
    private Long id;

    /**
     * 员工ID
     */
    @Schema(description = "员工ID")
    private Long employeeId;

    /**
     * 在岗月份 (如：2026-03)
     */
    @Schema(description = "在岗月份")
    private Integer workMonth;

    /**
     * 结算月份 (格式：YYYYMM)
     */
    @Schema(description = "结算月份 (YYYYMM)")
    private String settlementMonth;

    /**
     * 开始日期
     */
    @Schema(description = "周期开始日期")
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @Schema(description = "周期结束日期")
    private LocalDate endDate;

    /**
     * 月天数
     */
    @Schema(description = "本月自然天数")
    private BigDecimal monthDays;
    /**
     * 标准/制度月休天数 (如4.00, 6.00, 8.00)
     */
    @Schema(description = "标准/制度月休天数 (如4.00, 6.00, 8.00)")
    private BigDecimal standardRestDays;

    /**
     * 出勤天数
     */
    @Schema(description = "实际出勤天数")
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
    private BigDecimal fullAttendanceFlag;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 创建者
     */
    @Schema(description = "创建者")
    private String createBy;

    /**
     *  扩展字段：员工姓名
     */
    @Schema(description = "员工姓名")
    private String employeeName;
}
