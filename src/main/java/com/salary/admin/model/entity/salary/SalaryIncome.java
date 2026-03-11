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
 * 员工收入主表
 *
 * @author system
 * @since 2026-03-11
 */
@Schema(name = "SalaryIncome", description = "员工收入主表")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("salary_income")
public class SalaryIncome extends BaseEntity<SalaryIncome> {

    private static final long serialVersionUID = 1L;

    /**
     * 收入记录ID
     */
    @Schema(description = "收入记录ID")
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
     * 总收入金额
     */
    @Schema(description = "总收入金额")
    @TableField("total_amount")
    private BigDecimal totalAmount;


    @Override
    public Serializable pkVal() {
        return this.id;
    }
}