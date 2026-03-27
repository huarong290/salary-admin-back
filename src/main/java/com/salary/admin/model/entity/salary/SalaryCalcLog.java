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
 * 薪资计算日志表
 *
 * @author system
 * @since 2026-03-27
 */
@Schema(name = "SalaryCalcLog", description = "薪资计算日志表")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("salary_calc_log")
public class SalaryCalcLog extends BaseEntity<SalaryCalcLog> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 员工ID
     */
    @Schema(description = "员工ID")
    @TableField("employee_id")
    private Long employeeId;
    /**
     * 薪资周期ID
     */
    @Schema(description = "薪资周期ID")
    @TableField("period_id")
    private Long periodId;
    /**
     * 规则编码
     */
    @Schema(description = "规则编码")
    @TableField("rule_code")
    private String ruleCode;
    /**
     * 执行阶段
     */
    @Schema(description = "执行阶段")
    @TableField("stage")
    private Integer stage;
    /**
     * 输入参数
     */
    @Schema(description = "输入参数")
    @TableField("input_json")
    private String inputJson;
    /**
     * 输出结果
     */
    @Schema(description = "输出结果")
    @TableField("output_value")
    private BigDecimal outputValue;
    /**
     * 错误信息
     */
    @Schema(description = "错误信息")
    @TableField("error_msg")
    private String errorMsg;
    /**
     * 耗时(ms)
     */
    @Schema(description = "耗时(ms)")
    @TableField("execute_time")
    private Long executeTime;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}