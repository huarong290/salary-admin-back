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
 * 员工收入明细表
 *
 * @author system
 * @since 2026-03-11
 */
@Schema(name = "SalaryIncomeDetail", description = "员工收入明细表")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("salary_income_detail")
public class SalaryIncomeDetail extends BaseEntity<SalaryIncomeDetail> {

    private static final long serialVersionUID = 1L;

    /**
     * 明细ID
     */
    @Schema(description = "明细ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 薪资周期ID
     */
    @Schema(description = "薪资周期ID")
    @TableField("period_id")
    private Long periodId;
    /**
     * 员工ID
     */
    @Schema(description = "员工ID")
    @TableField("employee_id")
    private Long employeeId;
    /**
     * 收入类型ID
     */
    @Schema(description = "收入类型ID")
    @TableField("income_type_id")
    private Long incomeTypeId;
    /**
     * 收入类型名称
     */
    @Schema(description = "收入类型名称")
    @TableField("income_type_name")
    private String incomeTypeName;
    /**
     * 收入类型分类
     */
    @Schema(description = "收入类型分类")
    @TableField("category_name")
    private String categoryName;
    /**
     * 原币种 (如 CNY, PHP, USDT)
     */
    @Schema(description = "原币种 (如 CNY, PHP, USDT)")
    @TableField("currency")
    private String currency;
    /**
     * 原币金额
     */
    @Schema(description = "原币金额")
    @TableField("original_amount")
    private BigDecimal originalAmount;
    /**
     * 录入时汇率(原币兑本币
     */
    @Schema(description = "录入时汇率(原币兑本币)")
    @TableField("exchange_rate")
    private BigDecimal exchangeRate;
    /**
     * 收入金额
     */
    @Schema(description = "收入金额")
    @TableField("amount")
    private BigDecimal amount;
    /**
     * 备注说明
     */
    @Schema(description = "备注说明")
    @TableField("remark")
    private String remark;


    @Override
    public Serializable pkVal() {
        return this.id;
    }
}