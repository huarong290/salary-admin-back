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
 * 员工薪资标准配置表(含版本历史)
 *
 * @author system
 * @since 2026-03-27
 */
@Schema(name = "SalaryArchive", description = "员工薪资标准配置表(含版本历史)")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("salary_archive")
public class SalaryArchive extends BaseEntity<SalaryArchive> {

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
     * 版本号
     */
    @Schema(description = "版本号")
    @Version
    @TableField("version")
    private Integer version;
    /**
     * 是否最新版本 (1:是,0:否)
     */
    @Schema(description = "是否最新版本 (1:是,0:否)")
    @TableField("latest_flag")
    private Boolean latestFlag;
    /**
     * 生效日期
     */
    @Schema(description = "生效日期")
    @TableField("effective_date")
    private LocalDate effectiveDate;
    /**
     * 失效日期
     */
    @Schema(description = "失效日期")
    @TableField("expiry_date")
    private LocalDate expiryDate;
    /**
     * 审核状态 (0:草稿,1:已生效,2:驳回)
     */
    @Schema(description = "审核状态 (0:草稿,1:已生效,2:驳回)")
    @TableField("audit_status")
    private Boolean auditStatus;
    /**
     * 基本工资
     */
    @Schema(description = "基本工资")
    @TableField("base_salary")
    private BigDecimal baseSalary;
    /**
     * 全勤奖标准
     */
    @Schema(description = "全勤奖标准")
    @TableField("full_attendance_bonus")
    private BigDecimal fullAttendanceBonus;
    /**
     * 试用期底薪
     */
    @Schema(description = "试用期底薪")
    @TableField("probation_base_salary")
    private BigDecimal probationBaseSalary;
    /**
     * 结算币种
     */
    @Schema(description = "结算币种")
    @TableField("currency")
    private String currency;
    /**
     * 调薪原因
     */
    @Schema(description = "调薪原因")
    @TableField("change_reason")
    private String changeReason;
    /**
     * 个税规则Code
     */
    @Schema(description = "个税规则Code")
    @TableField("tax_rule_code")
    private String taxRuleCode;
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