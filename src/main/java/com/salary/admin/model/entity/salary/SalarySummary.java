package com.salary.admin.model.entity.salary;

import com.baomidou.mybatisplus.annotation.*;
import com.salary.admin.model.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
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
     * 薪资周期ID
     */
    @Schema(description = "薪资周期ID")
    @TableField("period_id")
    private Long periodId;
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