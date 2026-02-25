package com.salary.admin.model.dto.user;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 用户 DTO（Data Transfer Object）
 * 核心职责：输入对象_承载前端传来的“新增/修改”表单数据
 * <p>
 * 用于接收前端传递的用户数据，主要用于新增和更新操作。
 * 与数据库实体 SysUser 区分开，避免直接暴露数据库结构。
 */
@Data
@Schema(description = "用户新增/修改表单对象")
public class SysUserDTO {

    @Schema(description = "用户ID (新增时为空，修改时必填)", example = "1")
    private Long id;

    @Schema(description = "用户名 (登录账号)", requiredMode = Schema.RequiredMode.REQUIRED, example = "admin")
    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 30, message = "用户名长度必须在 4 到 30 个字符之间")
    private String username;

    @Schema(description = "密码 (新增时必填，修改时若为空则不修改)", example = "123456")
    // 注意：修改用户信息时前端通常不传密码（或传空），因此这里不加 @NotBlank，由 Service 层根据是新增还是修改做动态校验
    private String password;

    @Schema(description = "用户昵称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotBlank(message = "用户昵称不能为空")
    @Size(max = 30, message = "用户昵称长度不能超过 30 个字符")
    private String nickname;

    @Schema(description = "用户邮箱", example = "zhangsan@example.com")
    @Email(message = "邮箱格式不正确")
    private String email;

    @Schema(description = "手机号码", example = "13800138000")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确")
    private String phone;


    @Schema(description = "用户性别 (0未知 1男 2女)", example = "1")
    private Integer sex;

    @Schema(description = "头像路径", example = "https://example.com/avatar.png")
    private String avatar;

    @Schema(description = "帐号状态 (1正常 0停用)", example = "1")
    private Integer status;

}
