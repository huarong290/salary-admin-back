package com.salary.admin.model.dto.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 修改菜单请求参数
 * </p>
 *
 * @author system
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "修改菜单请求参数")
public class MenuEditReqDTO extends MenuAddReqDTO {

    @NotNull(message = "菜单主键ID不能为空")
    @Schema(description = "菜单ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;
}
