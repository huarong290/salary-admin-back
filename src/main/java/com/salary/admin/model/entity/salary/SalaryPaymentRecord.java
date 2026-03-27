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
import java.time.LocalDateTime;

/**
 * 薪资结算明细记录表
 *
 * @author system
 * @since 2026-03-27
 */
@Schema(name = "SalaryPaymentRecord", description = "薪资结算明细记录表")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("salary_payment_record")
public class SalaryPaymentRecord extends BaseEntity<SalaryPaymentRecord> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 汇总ID
     */
    @Schema(description = "汇总ID")
    @TableField("summary_id")
    private Long summaryId;
    /**
     * 员工ID
     */
    @Schema(description = "员工ID")
    @TableField("employee_id")
    private Long employeeId;
    /**
     * 支付流水号/批次号
     */
    @Schema(description = "支付流水号/批次号")
    @TableField("transaction_no")
    private String transactionNo;
    /**
     * 本次核销本位币金额
     */
    @Schema(description = "本次核销本位币金额")
    @TableField("base_amount")
    private BigDecimal baseAmount;
    /**
     * 结算币种
     */
    @Schema(description = "结算币种")
    @TableField("settlement_currency")
    private String settlementCurrency;
    /**
     * 汇率
     */
    @Schema(description = "汇率")
    @TableField("exchange_rate")
    private BigDecimal exchangeRate;
    /**
     * 实际到账金额 (base_amount * exchange_rate)
     */
    @Schema(description = "实际到账金额 (base_amount * exchange_rate)")
    @TableField("actual_amount")
    private BigDecimal actualAmount;
    /**
     * 支付方式
     */
    @Schema(description = "支付方式")
    @TableField("payment_method")
    private String paymentMethod;
    /**
     * 支付渠道/银行名称
     */
    @Schema(description = "支付渠道/银行名称")
    @TableField("payment_channel")
    private String paymentChannel;
    /**
     * 收款账号/钱包地址
     */
    @Schema(description = "收款账号/钱包地址")
    @TableField("target_account")
    private String targetAccount;
    /**
     * 支付单状态: 0-待处理, 1-支付中, 2-支付成功, 3-支付失败
     */
    @Schema(description = "支付单状态: 0-待处理, 1-支付中, 2-支付成功, 3-支付失败")
    @TableField("payment_status")
    private Byte paymentStatus;
    /**
     * 实际打款/到账时间
     */
    @Schema(description = "实际打款/到账时间")
    @TableField("payment_time")
    private LocalDateTime paymentTime;
    /**
     * 支付失败原因
     */
    @Schema(description = "支付失败原因")
    @TableField("error_msg")
    private String errorMsg;
    /**
     * 财务备注
     */
    @Schema(description = "财务备注")
    @TableField("remark")
    private String remark;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}