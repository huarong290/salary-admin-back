package com.salary.admin.model.vo.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 菜单 VO（View Object）
 * 核心职责：输出对象_专为前端渲染导航栏设计，包含嵌套树和 Meta 信息
 * <p>
 * 用于返回给前端的菜单数据，包含展示所需的字段。
 * 与 DTO 区分开，避免返回敏感信息。
 *
 * 特点：
 * - 包含 children 字段，用于构建树形结构
 * - 包含 meta 字段，前端路由常用的扩展信息
 */
@Data
@Schema(description = "动态菜单树节点")
public class MenuTreeVO {
    /**
     * 菜单ID
     */
    @Schema(description = "菜单ID")
    private Long id;
    /**
     * 父菜单ID
     */
    @Schema(description = "父菜单ID (顶级菜单为0)")
    private Long menuParentId;
    /**
     * 菜单名称
     */
    @Schema(description = "菜单名称")
    private String menuName;
    /**
     * 路由路径
     * 示例："/system/user"
     */
    @Schema(description = "路由地址 (如: /system/user)")
    private String menuPath;
    /**
     * 前端组件路径
     * 示例："system/user/index"
     */
    @Schema(description = "组件路径 (如: system/user/index)")
    private String menuComponent;
    /**
     * 重定向地址
     *
     */
    @Schema(description = "重定向地址")
    private String menuRedirect;
    /**
     * 权限标识
     * 示例："sys:user:add"
     */
    @Schema(description = "权限标识符 (如: sys:menu:list)")
    private String menuPermission;
    /**
     * 菜单图标
     * 示例："user"
     */
    @Schema(description = "图标 (如: user, setting)")
    private String menuIcon;
    /**
     * 菜单类型
     * 0 = 目录，1 = 菜单，2 = 按钮
     */
    @Schema(description = "菜单类型 (M目录 C菜单 F按钮)")
    private Integer menuType;
    /**
     * 是否显示
     */
    @Schema(description = "显示状态 (0显示 1隐藏)")
    private Integer menuVisible;
    /**
     * 排序值
     */
    @Schema(description = "排序值")
    private Integer menuSort;
    /**
     * 是否启用
     * 1 = 启用，0 = 禁用
     */
    @Schema(description = "是否启用:1-启用 0-禁用")
    private Integer menuStatus;
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 子菜单列表
     * 用于构建树形结构
     *
     * 使用 @Schema(implementation = SysMenuTreeVO.class) 避免 Swagger 在解析 List 泛型时触发反射异常。
     */
    @Schema(description = "子菜单列表", implementation = MenuTreeVO.class)
    private List<MenuTreeVO> children= new ArrayList<>();

    /**
     * meta 信息
     * 前端路由常用的扩展字段，例如标题、图标、是否缓存等
     */
    @Schema(description = "路由元信息")
    private MetaVO meta;
}
