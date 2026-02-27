package com.salary.admin.model.dto.role;

import com.salary.admin.model.dto.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "角色查询条件参数")
public class RoleQueryReqDTO extends PageQueryDTO {

    @Schema(description = "角色名称(模糊查询)")
    private String roleName;

    @Schema(description = "角色编码(精确查询)")
    private String roleCode;

    @Schema(description = "角色状态(1正常 0停用)")
    private Integer roleStatus;
}
