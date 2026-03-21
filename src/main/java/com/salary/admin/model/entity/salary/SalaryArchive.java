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
 * @since 2026-03-13
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
     * 版本号 (每次调薪递增)
     */
    @Schema(description = "版本号 (每次调薪递增)")
    @Version
    @TableField("version")
    private Integer version;
    /**
     * 是否当前最新版本: 0-历史, 1-最新
     */
    @Schema(description = "是否当前最新版本: 0-历史, 1-最新")
    @TableField("is_latest")
    private Integer isLatest;
    /**
     * 生效起始日期
     */
    @Schema(description = "生效起始日期")
    @TableField("effective_date")
    private LocalDate effectiveDate;
    /**
     * 失效日期
     */
    @Schema(description = "失效日期")
    @TableField("expiry_date")
    private LocalDate expiryDate;
    /**
     * 审核状态: 0-草稿/待审, 1-已生效, 2-驳回
     */
    @Schema(description = "审核状态: 0-草稿/待审, 1-已生效, 2-驳回")
    @TableField("audit_status")
    private Integer auditStatus;
    /**
     * 基本工资/转正底薪
     */
    @Schema(description = "基本工资/转正底薪")
    @TableField("base_salary")
    private BigDecimal baseSalary;
    /**
     * 全勤奖标准 (🌟 新增：对应数据库字段 full_attendance_bonus)
     */
    @Schema(description = "全勤奖标准")
    @TableField("full_attendance_bonus")
    private BigDecimal fullAttendanceBonus;
    /**
     * 试用期底薪(选填)
     */
    @Schema(description = "试用期底薪(选填)")
    @TableField("probation_base_salary")
    private BigDecimal probationBaseSalary;
    /**
     * 默认结算币种
     */
    @Schema(description = "默认结算币种")
    @TableField("currency")
    private String currency;
    /**
     * 调薪原因 (如: 年度普调、晋升)
     */
    @Schema(description = "调薪原因 (如: 年度普调、晋升)")
    @TableField("change_reason")
    private String changeReason;

    /**
     * 计税方案: 0-不计税, 1-居民个人所得税, 2-劳务报酬税
     */
    @Schema(description = "计税方案: 0-不计税, 1-居民个人所得税, 2-劳务报酬税")
    @TableField("tax_scheme")
    private Integer taxScheme;
    /**
     * 档案备注
     */
    @Schema(description = "档案备注")
    @TableField("remark")
    private String remark;


    @Override
    public Serializable pkVal() {
        return this.id;
    }
}