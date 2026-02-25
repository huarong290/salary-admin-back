package com.salary.admin.model.dto.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * 菜单 DTO（Data Transfer Object）
 * 核心职责：输入对象_承载前端传来的“新增/修改”表单数据
 * <p>
 * 用于接收前端传递的菜单数据，主要用于新增和更新操作。
 * 与数据库实体 SysMenu 区分开，避免直接暴露数据库结构。
 */
@Data
@Schema(description = "菜单保存/更新参数")
public class SysMenuDTO {

    /**
     * 菜单ID
     * 更新时必填，新增时可为空
     */
    @Schema(description = "菜单ID")
    private Long id;

    /**
     * 父菜单ID
     * 顶级菜单为 0
     */
    @NotNull(message = "父菜单ID不能为空")
    @Schema(description = "父菜单ID (顶级菜单为 0)")
    private Long menuParentId;

    /**
     * 菜单名称
     * 示例："用户管理"
     */
    @Schema(description = "菜单名称")
    private String menuName;
    /**
     * 对应数据库中的 unique 字段 menu_code
     */
    @NotBlank(message = "菜单编码不能为空")
    @Schema(description = "菜单编码 (唯一标识)")
    private String menuCode;
    /**
     * 路由路径
     * 示例："/system/user"
     */
    @Schema(description = "路由路径")
    private String menuPath;

    /**
     * 前端组件路径
     * 示例："system/user/index"
     */
    @Schema(description = "前端组件路径")
    private String menuComponent;
    /**
     * 重定向地址
     *
     */
    @Schema(description = "重定向地址")
    private String menuRedirect;
    /**
     * 菜单图标
     * 示例："user"
     */
    @Schema(description = "菜单图标")
    private String menuIcon;

    /**
     * 菜单类型
     * 0 = 目录，1 = 菜单，2 = 按钮
     */
    @NotNull(message = "菜单类型不能为空")
    @Range(min = 0, max = 2, message = "菜单类型非法")
    @Schema(description = "菜单类型:0-目录 1-菜单 2-按钮")
    private Integer menuType;

    /**
     * 权限标识
     * 示例："sys:user:add"
     */
    @Schema(description = "权限标识")
    private String menuPermission;

    /**
     * 排序值
     * 数值越小越靠前
     */
    @Schema(description = "排序值")
    private Integer menuSort;

    /**
     * 是否显示
     * 1 = 显示，0 = 隐藏
     */
    @Schema(description = "是否显示 (1:显示 0:隐藏)")
    private Integer menuVisible;

    /**
     * 是否启用
     * 1 = 启用，0 = 禁用
     */
    @Schema(description = "是否启用:1-启用 0-禁用")
    private Integer menuStatus;
}

