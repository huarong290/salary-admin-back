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

    public static void clear() {
        USER_HOLDER.remove();
    }
}
