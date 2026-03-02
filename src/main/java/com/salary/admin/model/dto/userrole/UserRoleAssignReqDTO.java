package com.salary.admin.model.dto.userrole;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "用户分配角色请求参数")
public class UserRoleAssignReqDTO {

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户主键ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @NotNull(message = "角色列表不能为null，但可以为空数组")
    @Schema(description = "角色ID列表(传空数组代表清空权限)", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> roleIds;
}
