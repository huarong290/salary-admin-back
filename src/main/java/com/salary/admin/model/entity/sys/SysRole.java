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
 * 角色表
 *
 * @author system
 * @since 2026-02-22
 */
@Schema(name = "SysRole", description = "角色表")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_role")
public class SysRole extends Model<SysRole> {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    @Schema(description = "角色ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 角色名称
     */
    @Schema(description = "角色名称")
    @TableField("role_name")
    private String roleName;
    /**
     * 角色编码 (如: ADMIN, HR_MGR)
     */
    @Schema(description = "角色编码 (如: ADMIN, HR_MGR)")
    @TableField("role_code")
    private String roleCode;
    /**
     * 显示顺序
     */
    @Schema(description = "显示顺序")
    @TableField("role_sort")
    private Integer roleSort;
    /**
     * 状态
     */
    @Schema(description = "状态")
    @TableField("role_status")
    private Integer roleStatus;
    /**
     * 角色描述
     */
    @Schema(description = "角色描述")
    @TableField("role_desc")
    private String roleDesc;
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