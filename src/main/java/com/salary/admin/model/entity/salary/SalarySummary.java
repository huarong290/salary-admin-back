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
import java.time.LocalDateTime;

/**
 * 薪资汇总与结算表
 *
 * @author system
 * @since 2026-03-11
 */
@Schema(name = "SalarySummary", description = "薪资汇总与结算表")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("salary_summary")
public class SalarySummary extends BaseEntity<SalarySummary> {

    private static final long serialVersionUID = 1L;

    /**
     * 汇总ID
     */
    @Schema(description = "汇总ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 员工ID
     */
    @Schema(description = "员工ID")
    @TableField("employee_id")
    private Long employeeId;
    /**
     * 员工code
     */
    @Schema(description = "员工code")
    @TableField("employee_code")
    private String employeeCode;
    /**
     * 员工姓名
     */
    @Schema(description = "员工姓名(快照)")
    @TableField("employee_name")
    private String employeeName;
    /**
     * 薪资周期ID
     */
    @Schema(description = "薪资周期ID")
    @TableField("period_id")
    private Long periodId;
    /**
     * 结算月份
     */
    @Schema(description = "结算月份")
    @TableField("settlement_month")
    private String settlementMonth;
    /**
     * 结算月份开始日期
     */
    @Schema(description = "周期开始(快照)")
    @TableField("period_start_date")
    private LocalDate periodStartDate;
    /**
     * 结算月份结束日期
     */
    @Schema(description = "周期结束(快照)")
    @TableField("period_end_date")
    private LocalDate periodEndDate;
    /**
     * 结算币种(CNY/PHP/USDT)
     */
    @Schema(description = "结算币种(CNY/PHP/USDT)")
    @TableField("currency")
    private String currency;
    /**
     * 汇率(1本币兑X目标币快照)
     */
    @Schema(description = "汇率(1本币兑X目标币快照)")
    @TableField("exchange_rate")
    private BigDecimal exchangeRate;
    /**
     * 应发小计(本币)
     */
    @Schema(description = "应发小计(本币)")
    @TableField("salary_subtotal")
    private BigDecimal salarySubtotal;
    /**
     * 扣款小计(本币)
     */
    @Schema(description = "扣款小计(本币)")
    @TableField("salary_deduction_total")
    private BigDecimal salaryDeductionTotal;
    /**
     * 最终结算薪资(本币)
     */
    @Schema(description = "最终结算薪资(本币)")
    @TableField("salary_total")
    private BigDecimal salaryTotal;
    /**
     * 实发金额(目标币)
     */
    @Schema(description = "实发金额(目标币)")
    @TableField("salary_converted")
    private BigDecimal salaryConverted;
    /**
     * 折合人民币(存档)
     */
    @Schema(description = "折合人民币(存档)")
    @TableField("salary_rmb")
    private BigDecimal salaryRmb;
    /**
     * 折合USDT(存档)
     */
    @Schema(description = "折合USDT(存档)")
    @TableField("salary_usdt")
    private BigDecimal salaryUsdt;
    /**
     * 发放账号/钱包地址(快照)
     */
    @Schema(description = "发放账号/钱包地址(快照)")
    @TableField("target_account")
    private String targetAccount;
    /**
     * 支付状态(0未支付 1已支付 2失败 3锁定)
     */
    @Schema(description = "支付状态(0未支付 1已支付 2失败 3锁定)")
    @TableField("payment_status")
    private Integer paymentStatus;
    /**
     * 实际发放/确认时间
     */
    @Schema(description = "实际发放/确认时间")
    @TableField("pay_time")
    private LocalDateTime payTime;
    /**
     * 备注
     */
    @Schema(description = "备注")
    @TableField("remark")
    private String remark;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}