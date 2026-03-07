package com.salary.admin.model.dto.rolemenu;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * 角色分配菜单请求参数
 * </p>
 */
@Data
@Schema(description = "角色分配菜单请求参数")
public class RoleMenuAssignReqDTO {

    @NotNull(message = "角色ID不能为空")
    @Schema(description = "角色主键ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long roleId;

    @NotNull(message = "菜单列表不能为null，但可以传空数组来清空权限")
    @Schema(description = "菜单ID列表 (传空数组代表清空该角色的所有权限)", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> menuIds;
}
