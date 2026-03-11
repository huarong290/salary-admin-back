package com.salary.admin.utils;

import com.salary.admin.model.dto.LoginUserDTO;

public class UserContextUtil {

    private static final ThreadLocal<LoginUserDTO> USER_HOLDER = new ThreadLocal<>();

    public static void setUser(LoginUserDTO user) {
        USER_HOLDER.set(user);
    }

    public static LoginUserDTO getUser() {
        return USER_HOLDER.get();
    }

    public static Long getUserId() {
        LoginUserDTO user = USER_HOLDER.get();
        return user != null ? user.getUserId() : null;
    }

    public static String getUsername() {
        LoginUserDTO user = USER_HOLDER.get();
        return user != null ? user.getUsername() : null;
    }
    /**
     * 🌟 新增：获取当前登录设备的唯一标识
     * 用于 AuthServiceImpl 中的设备会话管理
     */
    public static String getDeviceId() {
        LoginUserDTO user = USER_HOLDER.get();
        return user != null ? user.getDeviceId() : null;
    }
    /**
     * 判断当前登录用户是否具备管理员权限
     */
    public static boolean isAdmin() {
        LoginUserDTO user = USER_HOLDER.get();
        if (user == null) return false;

        // 1. 硬编码兜底：ID 为 1 的用户永远是超管
        if (Long.valueOf(1L).equals(user.getUserId())) {
            return true;
        }

        // 2. 标识位校验：检查登录时注入的 adminFlag
        return Boolean.TRUE.equals(user.getAdminFlag());
    }
    public static void clear() {
        USER_HOLDER.remove();
    }
}
