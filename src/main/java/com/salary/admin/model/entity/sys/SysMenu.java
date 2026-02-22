package com.salary.admin.model.entity.sys;

import io.swagger.v3.oas.annotations.media.Schema;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 权限菜单表
 *
 * @author system
 * @since 2026-02-22
 */
@Schema(name = "SysMenu", description = "权限菜单表")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_menu")
public class SysMenu extends Model<SysMenu> {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单ID
     */
    @Schema(description = "菜单ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 菜单名称
     */
    @Schema(description = "菜单名称")
    @TableField("menu_name")
    private String menuName;
    /**
     * 菜单编码 (唯一标识)
     */
    @Schema(description = "菜单编码 (唯一标识)")
    @TableField("menu_code")
    private String menuCode;
    /**
     * 前端路由地址
     */
    @Schema(description = "前端路由地址")
    @TableField("menu_path")
    private String menuPath;
    /**
     * 组件路径
     */
    @Schema(description = "组件路径")
    @TableField("menu_component")
    private String menuComponent;
    /**
     * 菜单图标，用于前端显示
     */
    @Schema(description = "菜单图标，用于前端显示")
    @TableField("menu_icon")
    private String menuIcon;
    /**
     * 权限标识 (如 sys:user:add)
     */
    @Schema(description = "权限标识 (如 sys:user:add)")
    @TableField("menu_permission")
    private String menuPermission;
    /**
     * 类型 (1:目录, 2:菜单, 3:按钮)
     */
    @Schema(description = "类型 (1:目录, 2:菜单, 3:按钮)")
    @TableField("menu_type")
    private Integer menuType;
    /**
     * 父级ID
     */
    @Schema(description = "父级ID")
    @TableField("menu_parent_id")
    private Long menuParentId;
    /**
     * 排序
     */
    @Schema(description = "排序")
    @TableField("menu_sort")
    private Integer menuSort;
    /**
     * 菜单是否可见：1可见 0隐藏，前端渲染控制
     */
    @Schema(description = "菜单是否可见：1可见 0隐藏，前端渲染控制")
    @TableField("menu_visible")
    private Integer menuVisible;
    /**
     * 状态
     */
    @Schema(description = "状态")
    @TableField("menu_status")
    private Integer menuStatus;
    /**
     * 删除标识 (0:未删, 1:已删)
     */
    @Schema(description = "删除标识 (0:未删, 1:已删)")
    @TableLogic
    @TableField("delete_flag")
    private Integer deleteFlag;
    /**
     * 创建者
     */
    @Schema(description = "创建者")
    @TableField("create_by")
    private String createBy;
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    /**
     * 修改者
     */
    @Schema(description = "修改者")
    @TableField("update_by")
    private String updateBy;
    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}