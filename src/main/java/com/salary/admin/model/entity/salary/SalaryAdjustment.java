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
 * 薪资周期专项调整表 (处理各类动态奖金与扣款)
 *
 * @author system
 * @since 2026-04-05
 */
@Schema(name = "SalaryAdjustment", description = "薪资周期专项调整表 (处理各类动态奖金与扣款)")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("salary_adjustment")
public class SalaryAdjustment extends BaseEntity<SalaryAdjustment> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 员工ID
     */
    @Schema(description = "员工ID")
    @TableField("employee_id")
    private Long employeeId;
    /**
     * 关联核算周期ID (本记录仅在该周期生效)
     */
    @Schema(description = "关联核算周期ID (本记录仅在该周期生效)")
    @TableField("period_id")
    private Long periodId;
    /**
     * 薪资项编码 (如：HOLIDAY_BONUS, LATE_DEDUCTION)
     */
    @Schema(description = "薪资项编码 (如：HOLIDAY_BONUS, LATE_DEDUCTION)")
    @TableField("item_code")
    private String itemCode;
    /**
     * 项目名称 (如：中秋节礼金, 迟到扣款)
     */
    @Schema(description = "项目名称 (如：中秋节礼金, 迟到扣款)")
    @TableField("item_name")
    private String itemName;
    /**
     * 原币种代码 (ISO 4217, 如 CNY, USD)
     */
    @Schema(description = "原币种代码 (ISO 4217, 如 CNY, USD)")
    @TableField("currency")
    private String currency;
    /**
     * 原币发生金额
     */
    @Schema(description = "原币发生金额")
    @TableField("original_amount")
    private BigDecimal originalAmount;
    /**
     * 当期核算汇率 (原币兑换本币的汇率)
     */
    @Schema(description = "当期核算汇率 (原币兑换本币的汇率)")
    @TableField("exchange_rate")
    private BigDecimal exchangeRate;
    /**
     * 折算本币金额 (实际参与引擎运算的金额)
     */
    @Schema(description = "折算本币金额 (实际参与引擎运算的金额)")
    @TableField("settlement_amount")
    private BigDecimal settlementAmount;
    /**
     * 调账类型: 1-增加(发钱), 2-扣减(扣钱)
     */
    @Schema(description = "调账类型: 1-增加(发钱), 2-扣减(扣钱)")
    @TableField("adjust_type")
    private Integer adjustType;
    /**
     * 数据来源: 1-手工录入, 2-系统生成, 3-API对接
     */
    @Schema(description = "数据来源: 1-手工录入, 2-系统生成, 3-API对接")
    @TableField("source_type")
    private Integer sourceType;
    /**
     * 状态: 0-草稿, 1-已生效(参与算薪)
     */
    @Schema(description = "状态: 0-草稿, 1-已生效(参与算薪)")
    @TableField("status")
    private Integer status;
    /**
     * 调账原因及备注 (审计依据)
     */
    @Schema(description = "调账原因及备注 (审计依据)")
    @TableField("remark")
    private String remark;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}