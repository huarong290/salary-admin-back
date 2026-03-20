package com.salary.admin.model.dto.role;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "修改角色请求参数")
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoleEditReqDTO extends RoleAddReqDTO {

    @NotNull(message = "角色ID不能为空")
    @Schema(description = "角色主键ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;
}
