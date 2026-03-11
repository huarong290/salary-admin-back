package com.salary.admin.model.dto.salary.employee;

import com.baomidou.mybatisplus.annotation.TableField;
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

    /**
     * 在职状态: 0-离职, 1-在职
     */
    @Schema(description = "在职状态: 0-离职, 1-在职")
    private Integer employmentStatus;
    /**
     * 是否转岗: 0-否, 1-是
     */
    @Schema(description = "是否转岗")
    private Integer isTransferred;
    /**
     * 住宿情况: 0-不住宿, 1-公司宿舍, 2-外宿补贴'
     */
    @Schema(description = "住宿情况: 0-不住宿, 1-公司宿舍, 2-外宿补贴'")
    private Integer accommodationStatus;
}
