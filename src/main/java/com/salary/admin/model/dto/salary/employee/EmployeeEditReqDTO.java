package com.salary.admin.model.dto.salary.employee;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 修改员工薪资档案 DTO
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "修改员工薪资档案请求")
public class EmployeeEditReqDTO extends EmployeeAddReqDTO {
    @NotNull(message = "员工ID不能为空")
    @Schema(description = "员工ID")
    private Long id;

    @Schema(description = "是否转岗")
    private Integer isTransferred;
}