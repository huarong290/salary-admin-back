package com.salary.admin.model.vo.salary.kpi;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 员工月度绩效考核结果视图对象 VO
 * 用于向前端页面展示完整、扁平化的绩效数据（通常需要联表查出员工姓名）
 *
 * @author system
 * @since 2026-04-04
 */
@Data
@Schema(description = "员工月度绩效考核视图对象")
public class SalaryKpiRecordVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    // ==================== 1. 员工基础信息 (联表聚合) ====================
    /**
     * 员工ID
     */
    @Schema(description = "员工ID")
    private Long employeeId;
    /**
     * 员工姓名 (聚合字段)
     */
    @Schema(description = "员工姓名 (聚合字段)")
    private String employeeName;
    /**
     * 员工工号 (聚合字段)
     */
    @Schema(description = "员工工号 (聚合字段)")
    private String employeeCode;
    /**
     * 所属部门 (聚合字段)
     */
    @Schema(description = "所属部门 (聚合字段)")
    private String departmentName;

    // ==================== 2. 周期与考核核心指标 ====================
    /**
     * 关联的薪资周期ID
     */
    @Schema(description = "关联的薪资周期ID")
    private Long periodId;
    /**
     * 考核/结算月份 (YYYYMM)
     */
    @Schema(description = "考核/结算月份 (YYYYMM)")
    private String settlementMonth;
    /**
     * 最终绩效评级 (如: A, B, C)
     */
    @Schema(description = "最终绩效评级 (如: A, B, C)")
    private String kpiGrade;
    /**
     * 考核打分
     */
    @Schema(description = "考核打分")
    private BigDecimal kpiScore;
    /**
     * 换算后的绩效系数 (如: 1.2000，核心算薪参数)
     */
    @Schema(description = "换算后的绩效系数 (如: 1.2000，核心算薪参数)")
    private BigDecimal kpiCoefficient;

    // ==================== 3. 审计与状态信息 ====================
    /**
     * 打分人/考核人
     */
    @Schema(description = "打分人/考核人")
    private String evaluateBy;
    /**
     * 考核评语/说明
     */
    @Schema(description = "考核评语/说明")
    private String evaluateRemark;
    /**
     * 审核流转状态: 0-打分中/草稿, 1-已确认/定稿, 2-申诉中
     */
    @Schema(description = "审核流转状态: 0-打分中/草稿, 1-已确认/定稿, 2-申诉中")
    private Integer auditStatus;
    /**
     * 版本生效标识: 1-当前生效, 0-历史作废
     */
    @Schema(description = "版本生效标识: 1-当前生效, 0-历史作废")
    private Integer effectiveFlag;
    /**
     * 最近一次操作时间
     */
    @Schema(description = "最近一次操作时间")
    private LocalDateTime updateTime;
}