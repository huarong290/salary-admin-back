package com.salary.admin.model.dto.role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "新增角色请求参数")
public class RoleAddReqDTO {

    @NotBlank(message = "角色名称不能为空")
    @Schema(description = "角色名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String roleName;

    @NotBlank(message = "角色编码不能为空")
    @Schema(description = "角色编码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String roleCode;

    @NotNull(message = "显示顺序不能为空")
    @Schema(description = "显示顺序", example = "0")
    private Integer roleSort;

    @Schema(description = "状态 (1正常 0停用)", example = "1")
    private Integer roleStatus;

    @Schema(description = "角色描述")
    private String roleDesc;

    @Schema(description = "备注")
    private String remark;
}