package com.salary.admin.model.dto.salary.employee;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 新增员工薪资档案 DTO
 */
@Data
@Schema(description = "新增员工薪资档案请求")
public class EmployeeAddReqDTO implements Serializable {
    @NotBlank(message = "员工编号不能为空")
    @Schema(description = "员工编号")
    private String employeeCode;

    @NotBlank(message = "姓名不能为空")
    @Schema(description = "姓名")
    private String employeeName;

    @Schema(description = "所属公司")
    private String companyName;

    @Schema(description = "部门")
    private String department;

    @Schema(description = "在职状态")
    private String employmentStatus;

    @Schema(description = "住宿情况")
    private String accommodationStatus;
}
