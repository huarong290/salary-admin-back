package com.salary.admin.model.dto.salary.employee;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeEditReqDTO extends EmployeeAddReqDTO {
    /**
     * 员工ID
     */
    @NotNull(message = "员工ID不能为空")
    @Schema(description = "员工ID")
    private Long id;
    /**
     * 是否转岗: 0-否, 1-是
     */
    @Schema(description = "是否转岗: 0-否, 1-是")
    private Integer isTransferred;
}