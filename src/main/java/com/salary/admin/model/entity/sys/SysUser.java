package com.salary.admin.model.entity.sys;

import com.salary.admin.model.entity.base.BaseEntity;
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
 * 用户表
 *
 * @author system
 * @since 2026-02-22
 */
@Schema(name = "SysUser", description = "用户表")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_user")
public class SysUser extends BaseEntity<SysUser> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 用户名
     */
    @Schema(description = "用户名")
    @TableField("username")
    private String username;
    /**
     * 加密密码
     */
    @Schema(description = "加密密码")
    @TableField("password")
    private String password;
    /**
     * 用户昵称
     */
    @Schema(description = "用户昵称")
    @TableField("nickname")
    private String nickname;
    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    @TableField("email")
    private String email;
    /**
     * 手机号
     */
    @Schema(description = "手机号")
    @TableField("phone")
    private String phone;
    /**
     * 状态 (0:禁用, 1:正常)
     */
    @Schema(description = "状态 (0:禁用, 1:正常)")
    @TableField("status")
    private Integer status;
    /**
     * 用户性别 (0:未知, 1:男, 2:女)
     */
    @Schema(description = "用户性别 (0:未知, 1:男,2:女)")
    @TableField("sex")
    private Integer sex;
    /**
     * 头像地址URL
     */
    @Schema(description = "头像地址URL")
    @TableField("avatar")
    private String avatar;
    /**
     * 最后登录时间
     */
    @Schema(description = "最后登录时间")
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;

    /**
     * 必须重写 pkVal，告诉 Model 你的主键是哪个字段
     */
    @Override
    public Serializable pkVal() {
        return this.id;
    }
}