package com.salary.admin.model.dto.user;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "新增用户请求参数")
public class UserAddReqDTO {

    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名 (登录账号)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Schema(description = "登录密码 (不传则使用系统默认密码)")
    private String password;

    @NotBlank(message = "用户昵称不能为空")
    @Schema(description = "用户昵称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nickname;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "状态 (0:禁用, 1:正常)")
    private Integer status = 1;

    @Schema(description = "性别 (0:未知, 1:男, 2:女)")
    private Integer sex = 0;
}
