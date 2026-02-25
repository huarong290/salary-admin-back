package com.salary.admin.model.vo.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 菜单 VO（View Object）
 * <p>
 * 用于返回给前端的菜单数据，包含展示所需的字段。
 * 与 DTO 区分开，避免返回敏感信息。
 */
@Data
public class SysMenuVO {

    /**
     * 菜单ID
     */
    @Schema(description = "菜单ID")
    private Long id;

    /**
     * 父菜单ID
     */
    @Schema(description = "父菜单ID")
    private Long menuParentId;

    /**
     * 菜单名称
     */
    @Schema(description = "菜单名称")
    private String menuName;

    /**
     * 菜单编码 (唯一标识)
     */
    @Schema(description = "菜单编码 (唯一标识)")
    private String menuCode;

    /**
     * 路由路径
     */
    @Schema(description = "路由路径")
    private String menuPath;

    /**
     * 前端组件路径
     */
    @Schema(description = "前端组件路径")
    private String menuComponent;

    /**
     * 菜单图标
     */
    @Schema(description = "菜单图标")
    private String menuIcon;

    /**
     * 菜单类型
     * 0 = 目录，1 = 菜单，2 = 按钮
     */
    @Schema(description = "菜单类型:0-目录 1-菜单 2-按钮")
    private Integer menuType;

    /**
     * 权限标识
     */
    @Schema(description = "权限标识")
    private String menuPermission;

    /**
     * 排序值
     */
    @Schema(description = "排序值")
    private Integer menuSort;

    /**
     * 是否显示
     */
    @Schema(description = "是否显示 (1:显示 0:隐藏)")
    private Integer menuVisible;

    /**
     * 是否启用
     */
    @Schema(description = "是否启用:1-启用 0-禁用")
    private Integer menuStatus;
// 移除了 deleteFlag，因为它属于内部逻辑字段
//    /**
//     * 是否删除
//     */
//    @Schema(description = "是否删除")
//    private Integer deleteFlag;

    /**
     * 创建者
     */
    @Schema(description = "创建者")
    private String createBy;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 修改者
     */
    @Schema(description = "修改者")
    private String updateBy;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    private LocalDateTime updateTime;
}
