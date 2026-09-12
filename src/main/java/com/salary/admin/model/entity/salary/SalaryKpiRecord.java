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
 * 员工月度绩效考核记录表
 *
 * @author system
 * @since 2026-04-04
 */
@Schema(name = "SalaryKpiRecord", description = "员工月度绩效考核记录表")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("salary_kpi_record")
public class SalaryKpiRecord extends BaseEntity<SalaryKpiRecord> {

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
     * 关联薪资周期ID (硬关联：确保绩效与发薪周期绝对对齐)
     */
    @Schema(description = "关联薪资周期ID (硬关联：确保绩效与发薪周期绝对对齐)")
    @TableField("period_id")
    private Long periodId;
    /**
     * 考核/结算月份 (格式: YYYYMM，方便按月快速检索)
     */
    @Schema(description = "考核/结算月份 (格式: YYYYMM，方便按月快速检索)")
    @TableField("settlement_month")
    private String settlementMonth;
    /**
     * 最终绩效评级 (例如: S, A, B, C, D 等)
     */
    @Schema(description = "最终绩效评级 (例如: S, A, B, C, D 等)")
    @TableField("kpi_grade")
    private String kpiGrade;
    /**
     * 最终考核打分 (例如: 95.50，用于精细化计算)
     */
    @Schema(description = "最终考核打分 (例如: 95.50，用于精细化计算)")
    @TableField("kpi_score")
    private BigDecimal kpiScore;
    /**
     * 绩效发放系数 (核心参数：例如 1.2000，算薪引擎直接乘以绩效基数)
     */
    @Schema(description = "绩效发放系数 (核心参数：例如 1.2000，算薪引擎直接乘以绩效基数)")
    @TableField("kpi_coefficient")
    private BigDecimal kpiCoefficient;
    /**
     * 考核人 (通常记录直属主管或HR的账号/工号)
     */
    @Schema(description = "考核人 (通常记录直属主管或HR的账号/工号)")
    @TableField("evaluate_by")
    private String evaluateBy;
    /**
     * 考核评语/说明 (用于申诉或审计备查)
     */
    @Schema(description = "考核评语/说明 (用于申诉或审计备查)")
    @TableField("evaluate_remark")
    private String evaluateRemark;
    /**
     * 审核流转状态 (0:打分中/草稿, 1:已确认/审核通过, 2:被驳回/申诉中。注：引擎只抓取=1的数据)
     */
    @Schema(description = "审核流转状态 (0:打分中/草稿, 1:已确认/审核通过, 2:被驳回/申诉中。注：引擎只抓取=1的数据)")
    @TableField("audit_status")
    private Integer auditStatus;
    /**
     * 版本生效标识 (1:当前生效版本, 0:历史作废版本。用于处理重新打分时的历史数据保留)
     */
    @Schema(description = "版本生效标识 (1:当前生效版本, 0:历史作废版本。用于处理重新打分时的历史数据保留)")
    @TableField("effective_flag")
    private Integer effectiveFlag;
    /**
     * 计税标识: NULL-继承全局/档案, 0-不计税, 1-计税
     */
    @Schema(description = "计税标识: NULL-继承全局/档案, 0-不计税, 1-计税")
    @TableField("taxable_flag")
    private Integer taxableFlag;


    @Override
    public Serializable pkVal() {
        return this.id;
    }
}