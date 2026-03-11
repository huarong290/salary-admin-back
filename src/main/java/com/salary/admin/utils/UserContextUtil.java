package com.salary.admin.utils;

import com.salary.admin.constants.role.RoleConstants;
import com.salary.admin.exception.BusinessException;
import com.salary.admin.model.dto.LoginUserDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

/**
 * 用户上下文工具类 (ThreadLocal 实现)
 *
 * <p>
 * 用于在一次 HTTP 请求生命周期内存储当前登录用户信息。
 * </p>
 *
 * <p>设计原则：</p>
 * <ul>
 *     <li>线程隔离，避免多线程污染</li>
 *     <li>统一获取用户上下文</li>
 *     <li>避免业务层直接依赖 SecurityContext</li>
 * </ul>
 *
 * <p>注意：</p>
 * 必须在过滤器 finally 中调用 {@link #clear()} 防止 ThreadLocal 泄漏
 */
@Slf4j
public class UserContextUtil {
    // 私有构造，工具类不应该被初始化
    private UserContextUtil() {}

    /**
     * 当前线程用户上下文
     */
    private static final ThreadLocal<LoginUserDTO> USER_HOLDER = new ThreadLocal<>();

    /**
     * 设置当前登录用户
     */
    public static void setUser(LoginUserDTO user) {
        if (user == null) {
            log.warn("尝试设置空用户上下文");
            return;
        }
        USER_HOLDER.set(user);
    }

    /**
     * 获取当前登录用户对象
     */
    public static LoginUserDTO getUser() {
        return USER_HOLDER.get();
    }

    /**
     * 是否已登录
     */
    public static boolean isLogin() {
        return USER_HOLDER.get() != null;
    }

    /**
     * 获取当前用户ID
     */
    public static Long getUserId() {
        return Optional.ofNullable(USER_HOLDER.get())
                .map(LoginUserDTO::getUserId)
                .orElse(null);
    }

    /**
     * 获取当前用户名
     */
    public static String getUsername() {
        return Optional.ofNullable(USER_HOLDER.get())
                .map(LoginUserDTO::getUsername)
                .orElse("anonymous");
    }

    /**
     * 获取当前设备ID
     */
    public static String getDeviceId() {
        return Optional.ofNullable(USER_HOLDER.get())
                .map(LoginUserDTO::getDeviceId)
                .orElse(null);
    }

    /**
     * 获取当前用户角色
     */
    public static Set<String> getRoles() {
        return Optional.ofNullable(USER_HOLDER.get())
                .map(LoginUserDTO::getRoles)
                .orElse(Collections.emptySet());
    }

    /**
     * 获取当前用户权限
     */
    public static Set<String> getPermissions() {
        return Optional.ofNullable(USER_HOLDER.get())
                .map(LoginUserDTO::getPermissions)
                .orElse(Collections.emptySet());
    }

    /**
     * 判断用户是否拥有某角色
     */
    public static boolean hasRole(String roleCode) {
        // 增加空校验逻辑
        if (StringUtils.isBlank(roleCode)) {
            return false;
        }
        return getRoles().contains(roleCode);
    }

    /**
     * 判断是否超级管理员
     */
    public static boolean isSuperAdmin() {
        return hasRole(RoleConstants.SUPER_ADMIN);
    }

    /**
     * 判断是否管理员
     */
    public static boolean isAdmin() {
        return hasRole(RoleConstants.SUPER_ADMIN) || hasRole(RoleConstants.ADMIN);
    }

    /**
     * 判断是否拥有某权限
     */
    public static boolean hasPermission(String permission) {
        //  增加权限标识判空
        if (StringUtils.isBlank(permission)) {
            return false;
        }
        return getPermissions().contains(permission);
    }

    /**
     * 清理 ThreadLocal
     * 防止线程复用导致用户数据污染
     */
    public static void clear() {
        USER_HOLDER.remove();
    }

    public static LoginUserDTO requireUser() {
        LoginUserDTO user = USER_HOLDER.get();
        if (user == null) {
            throw new BusinessException("当前线程未绑定用户上下文");
        }
        return user;
    }
}