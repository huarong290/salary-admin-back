package com.salary.admin.model.dto.salary.employee;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 新增员工薪资档案 DTO
 */
@Data
@Schema(description = "新增员工薪资档案请求")
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeAddReqDTO implements Serializable {
    /**
     * 员工编号
     */
    @NotBlank(message = "员工编号不能为空")
    @Schema(description = "员工编号")
    private String employeeCode;
    /**
     * 姓名
     */
    @NotBlank(message = "姓名不能为空")
    @Schema(description = "姓名")
    private String employeeName;
    /**
     * 所属公司
     */
    @Schema(description = "所属公司")
    private String companyName;
    /**
     * 部门
     */
    @Schema(description = "部门")
    private String department;
    /**
     * 岗位名称/职级
     */
    @Schema(description = "岗位名称/职级")
    private String jobTitle;
    /**
     * 在职状态: 0-离职, 1-在职
     */
    @Schema(description = "在职状态: 0-离职, 1-在职")
    private Integer employmentStatus;
    /**
     * 是否转岗: 0-否, 1-是
     */
    @Schema(description = "是否转岗: 0-否, 1-是")
    private Integer transferFlag;
    /**
     * 住宿情况: 0-不住宿, 1-公司宿舍, 2-外宿补贴'
     */
    @Schema(description = "住宿情况: 0-不住宿, 1-公司宿舍, 2-外宿补贴'")
    private Integer accommodationStatus;

    /**
     * 平台账号
     */
    @Schema(description = "平台账号")
    private String platformAccount;

    /**
     * 入职日期
     */
    @Schema(description = "入职日期")
    private LocalDate entryDate;

    /**
     * 预计转正日期
     */
    @Schema(description = "预计转正日期")
    private LocalDate probationEndDate;

    /**
     * 实际离职日期
     */
    @Schema(description = "实际离职日期")
    private LocalDate actualLeaveDate;
}
