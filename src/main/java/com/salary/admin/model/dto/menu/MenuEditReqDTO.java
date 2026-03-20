package com.salary.admin.model.dto.menu;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class MenuEditReqDTO extends MenuAddReqDTO {

    @NotNull(message = "菜单主键ID不能为空")
    @Schema(description = "菜单ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;
}
