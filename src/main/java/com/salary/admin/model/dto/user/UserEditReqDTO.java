package com.salary.admin.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "修改用户请求参数")
public class UserEditReqDTO {

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    // 💡 注意：修改时通常不允许改登录账号(username)，所以这里不放 username 字段

    @Schema(description = "用户昵称")
    private String nickname;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "状态 (0:禁用, 1:正常)")
    private Integer status;

    @Schema(description = "性别 (0:未知, 1:男, 2:女)")
    private Integer sex;
}