package com.salary.admin.model.entity.salary;

import com.baomidou.mybatisplus.annotation.*;
import com.salary.admin.model.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 薪资汇总与结算表
 *
 * @author system
 * @since 2026-03-27
 */
@Schema(name = "SalarySummary", description = "薪资汇总与结算表")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "salary_summary")
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
     * 员工编号快照
     */
    @Schema(description = "员工编号快照")
    @TableField("employee_code")
    private String employeeCode;
    /**
     * 员工姓名快照
     */
    @Schema(description = "员工姓名快照")
    @TableField("employee_name")
    private String employeeName;
    /**
     * 薪资周期ID
     */
    @Schema(description = "薪资周期ID")
    @TableField("period_id")
    private Long periodId;
    /**
     * 结算月份 (YYYYMM)
     */
    @Schema(description = "结算月份 (YYYYMM)")
    @TableField("settlement_month")
    private String settlementMonth;
    /**
     * 周期开始快照
     */
    @Schema(description = "周期开始快照")
    @TableField("period_start_date")
    private LocalDate periodStartDate;
    /**
     * 周期结束快照
     */
    @Schema(description = "周期结束快照")
    @TableField("period_end_date")
    private LocalDate periodEndDate;
    /**
     * 收入合计（item_type=1）
     */
    @Schema(description = "收入合计（item_type=1）")
    @TableField("income_total")
    private BigDecimal incomeTotal;
    /**
     * 扣款合计（item_type=2）
     */
    @Schema(description = "扣款合计（item_type=2）")
    @TableField("deduction_total")
    private BigDecimal deductionTotal;
    /**
     * 税费合计（item_type=3）
     */
    @Schema(description = "税费合计（item_type=3）")
    @TableField("tax_total")
    private BigDecimal taxTotal;
    /**
     * 应发工资（税前）通常 = income_total
     */
    @Schema(description = "应发工资（税前）通常 = income_total")
    @TableField("gross_salary")
    private BigDecimal grossSalary;
    /**
     * 实发工资（最终） net = income - deduction - tax
     */
    @Schema(description = "实发工资（最终） net = income - deduction - tax")
    @TableField("net_salary")
    private BigDecimal netSalary;
    /**
     * 计算版本号(用于重算/历史追溯)
     */
    @Schema(description = "计算版本号(用于重算/历史追溯)")
    @Version
    @TableField("calc_version")
    private Integer calcVersion;
    /**
     *  计算状态:0-未计算 1-成功 2-失败
     */
    @Schema(description = " 计算状态:0-未计算 1-成功 2-失败")
    @TableField("calc_status")
    private Integer calcStatus;
    /**
     * 发放状态：0-未支付 1-已支付 2-支付失败
     */
    @Schema(description = "发放状态：0-未支付 1-已支付 2-支付失败")
    @TableField("payment_status")
    private Integer paymentStatus;
    /**
     * 是否锁定(1:锁定, 0:未锁定, 发放后锁定不可重算)
     */
    @Schema(description = "是否锁定(1:锁定, 0:未锁定, 发放后锁定不可重算)")
    @TableField("lock_flag")
    private Integer lockFlag;
    /**
     * 汇总快照(用于展示工资单):{\"income\": [...],\"deduction\": [...],\"tax\": [...]}
     */
    @Schema(description = "汇总快照(用于展示工资单):{\"income\": [...],\"deduction\": [...],\"tax\": [...]}")
    @TableField(value = "detail_json")
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