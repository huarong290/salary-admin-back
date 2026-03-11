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

/**
 * 员工基本信息表
 *
 * @author system
 * @since 2026-03-11
 */
@Schema(name = "SalaryEmployee", description = "员工基本信息表")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("salary_employee")
public class SalaryEmployee extends BaseEntity<SalaryEmployee> {

    private static final long serialVersionUID = 1L;

    /**
     * 员工ID
     */
    @Schema(description = "员工ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 员工编号
     */
    @Schema(description = "员工编号")
    @TableField("employee_code")
    private String employeeCode;
    /**
     * 姓名
     */
    @Schema(description = "姓名")
    @TableField("employee_name")
    private String employeeName;
    /**
     * 所属公司
     */
    @Schema(description = "所属公司")
    @TableField("company_name")
    private String companyName;
    /**
     * 部门
     */
    @Schema(description = "部门")
    @TableField("department")
    private String department;
    /**
     * 在职状态: 0-离职, 1-在职
     */
    @Schema(description = "在职状态: 0-离职, 1-在职")
    @TableField("employment_status")
    private Integer employmentStatus;
    /**
     * 是否转岗: 0-否, 1-是
     */
    @Schema(description = "是否转岗")
    @TableField("is_transferred")
    private Integer isTransferred;
    /**
     * 住宿情况: 0-不住宿, 1-公司宿舍, 2-外宿补贴'
     */
    @Schema(description = "住宿情况: 0-不住宿, 1-公司宿舍, 2-外宿补贴'")
    @TableField("accommodation_status")
    private Integer accommodationStatus;


    @Override
    public Serializable pkVal() {
        return this.id;
    }
}