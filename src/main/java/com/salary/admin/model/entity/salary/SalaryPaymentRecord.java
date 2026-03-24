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

/**
 * 薪资结算明细记录表
 *
 * @author system
 * @since 2026-03-15
 */
@Schema(name = "SalaryPaymentRecord", description = "薪资结算明细记录表")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("salary_payment_record")
public class SalaryPaymentRecord extends BaseEntity<SalaryPaymentRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @Schema(description = "记录ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 关联汇总ID
     */
    @Schema(description = "关联汇总ID")
    @TableField("summary_id")
    private Long summaryId;
    /**
     * 员工ID
     */
    @Schema(description = "员工ID")
    @TableField("employee_id")
    private Long employeeId;
    /**
     * 关联薪资档案版本ID(系统计算必填)
     */
    @Schema(description = "关联薪资档案版本ID(系统计算必填)")
    @TableField("archive_id")
    private Long archiveId;
    /**
     * 基本工资(系统计算快照)
     */
    @Schema(description = "基本工资(系统计算快照)")
    @TableField("base_salary")
    private BigDecimal baseSalary;
    /**
     * 收入合计
     */
    @Schema(description = "收入合计")
    @TableField("income_total")
    private BigDecimal incomeTotal;
    /**
     * 扣款合计
     */
    @Schema(description = "扣款合计")
    @TableField("deduction_total")
    private BigDecimal deductionTotal;
    /**
     * 最终总计(无论是计算还是手动录入)
     */
    @Schema(description = "最终总计(无论是计算还是手动录入)")
    @TableField("final_salary")
    private BigDecimal finalSalary;
    /**
     * 是否手动录入总额(0系统计算 1手动录入)
     */
    @Schema(description = "是否手动录入总额(0系统计算 1手动录入)")
    @TableField("is_manual")
    private Integer isManual;
    /**
     * 结算币种(CNY/USD/PHP等)
     */
    @Schema(description = "结算币种(CNY/USD/PHP等)")
    @TableField("settlement_currency")
    private String settlementCurrency;

    /**
     * 核算汇率(相对于系统本位币)
     */
    @Schema(description = "核算汇率(相对于系统本位币)")
    @TableField("exchange_rate")
    private BigDecimal exchangeRate;
    /**
     * 折合本位币实发金额(用于汇总报表)
     */
    @Schema(description = "折合本位币实发金额(用于汇总报表)")
    @TableField("base_final_salary")
    private BigDecimal baseFinalSalary;
    /**
     * 发放方式(银行卡/USDT地址/现金)
     */
    @Schema(description = "发放方式(银行卡/USDT地址/现金)")
    @TableField("payment_method")
    private String paymentMethod;

    @Schema(description = "拆分发放：法币(CNY/PHP)部分金额")
    @TableField("split_cny_amount")
    private BigDecimal splitCnyAmount;

    @Schema(description = "拆分发放：加密货币(USDT)部分金额")
    @TableField("split_usdt_amount")
    private BigDecimal splitUsdtAmount;

    @Schema(description = "当期 USDT 兑法币的结算汇率")
    @TableField("usdt_exchange_rate")
    private BigDecimal usdtExchangeRate;

    @Schema(description = "法币部分发放状态(0未发 1已发)")
    @TableField("cny_pay_status")
    private Integer cnyPayStatus;

    @Schema(description = "USDT部分发放状态(0未发 1已发)")
    @TableField("usdt_pay_status")
    private Integer usdtPayStatus;

    @Schema(description = "计算版本")
    @TableField("version")
    private Integer version;

    @Schema(description = "是否有效(0无效 1有效)")
    @TableField("valid_flag")
    private Integer validFlag;
    /**
     * 计算详情快照(存储当时所有income/deduction的JSON)
     */
    @Schema(description = "计算详情快照(存储当时所有income/deduction的JSON)")
    @TableField("detail_json")
    private String detailJson;
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