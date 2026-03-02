package com.salary.admin.model.dto.menu;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * <p>
 * 新增菜单请求参数
 * </p>
 *
 * @author system
 */
@Data
@Schema(description = "新增菜单请求参数")
public class MenuAddReqDTO {

    @NotNull(message = "父菜单ID不能为空")
    @Schema(description = "父菜单ID (顶级目录传0)", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long menuParentId;

    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 50, message = "菜单名称长度不能超过50个字符")
    @Schema(description = "菜单名称", example = "系统管理", requiredMode = Schema.RequiredMode.REQUIRED)
    private String menuName;

    @NotBlank(message = "菜单编码不能为空")
    @Size(max = 100, message = "菜单编码长度不能超过100个字符")
    @Schema(description = "菜单编码 (唯一标识)", example = "sys_manage", requiredMode = Schema.RequiredMode.REQUIRED)
    private String menuCode;

    @NotNull(message = "菜单类型不能为空")
    @Schema(description = "菜单类型 (1:目录, 2:菜单, 3:按钮)", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer menuType;

    @NotNull(message = "显示顺序不能为空")
    @Schema(description = "显示顺序 (数值越小越靠前)", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer menuSort;

    // --- 以下为非必填扩展字段，根据 menuType 动态使用 ---

    @Size(max = 200, message = "路由地址长度不能超过200个字符")
    @Schema(description = "前端路由地址 (目录或菜单使用)", example = "/system")
    private String menuPath;

    @Size(max = 200, message = "组件路径长度不能超过200个字符")
    @Schema(description = "前端组件路径 (菜单使用)", example = "system/user/index")
    private String menuComponent;

    @Size(max = 200, message = "重定向地址长度不能超过200个字符")
    @Schema(description = "重定向地址")
    private String menuRedirect;

    @Size(max = 50, message = "菜单图标长度不能超过50个字符")
    @Schema(description = "菜单图标", example = "system")
    private String menuIcon;

    @Size(max = 100, message = "权限标识长度不能超过100个字符")
    @Schema(description = "后端权限标识 (按钮类型使用)", example = "sys:user:add")
    private String menuPermission;

    @Schema(description = "菜单是否可见 (1:可见 0:隐藏)", example = "1")
    private Integer menuVisible;

    @Schema(description = "菜单业务状态 (1:正常 0:停用)", example = "1")
    private Integer menuStatus;
}