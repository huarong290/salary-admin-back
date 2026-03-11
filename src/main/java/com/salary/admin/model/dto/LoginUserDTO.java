package com.salary.admin.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * 当前登录用户上下文信息 DTO
 *
 * <p>
 * 该对象用于在一次 HTTP 请求生命周期内，
 * 通过 {@link com.salary.admin.utils.UserContextUtil} 的 ThreadLocal
 * 存储当前登录用户的核心信息，供业务层随时获取。
 * </p>
 *
 * <p>
 * 设计原则：
 * 1. 登录成功后只查询一次数据库
 * 2. 将用户基础信息、角色信息、权限信息全部放入该对象
 * 3. 后续业务逻辑不再重复查询数据库
 * </p>
 *
 * <p>
 * 典型使用场景：
 * - 操作日志记录（记录操作人）
 * - 权限校验
 * - 数据权限控制
 * - 设备会话管理
 * </p>
 *
 * @author system
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginUserDTO {

    /**
     * 当前登录用户ID
     */
    private Long userId;

    /**
     * 当前登录用户名
     */
    private String username;

    /**
     * 当前登录设备唯一标识
     *
     * <p>
     * 用于设备会话管理（如：多端登录控制）
     * </p>
     */
    private String deviceId;

    /**
     * 当前用户拥有的角色编码集合
     *
     * <p>
     * 数据来源：
     * sys_user_role + sys_role.role_code
     *
     * 示例：
     * ["SUPER_ADMIN", "ADMIN"]
     * </p>
     *
     * <p>
     * 典型用途：
     * 判断用户角色，例如：
     * roles.contains(RoleConstants.SUPER_ADMIN)
     * </p>
     */
    private Set<String> roles;

    /**
     * 当前用户拥有的权限标识集合
     *
     * <p>
     * 数据来源：
     * sys_role_menu + sys_menu.permission
     * </p>
     *
     * 示例：
     * [
     *  "salary:employee:add",
     *  "salary:employee:edit",
     *  "salary:employee:del"
     * ]
     *
     * <p>
     * 典型用途：
     * - 后端接口权限校验
     * - 前端按钮权限控制
     * </p>
     */
    private Set<String> permissions;

}