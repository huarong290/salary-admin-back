package com.salary.admin.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JWT令牌刷新请求数据传输对象
 * 用于接收客户端提交的刷新令牌请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenRefreshReqDTO {
    /**
     * 刷新令牌字符串
     * @NotBlank 验证注解确保该字段不能为空
     * @pattern 可添加正则验证令牌格式（可选）
     */
    @NotBlank(message = "刷新令牌不能为空")
    private String refreshToken;

    /**
     * 客户端设备标识
     * 用于多设备登录场景下的令牌管理（可选字段）
     */
    private String deviceId;
}

