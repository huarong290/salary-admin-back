package com.salary.admin.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 承载当前线程用户信息的对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginUserDTO {
    /**
     * 当前用户ID
     */
    private Long userId;
    /**
     * 当前用户名
     */
    private String username;
    /**
     * 当前设备ID
     */
    private String deviceId;
}
