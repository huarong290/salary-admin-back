package com.salary.admin.model.dto.user;

import com.salary.admin.model.vo.menu.MenuTreeVO;
import com.salary.admin.model.vo.user.SysUserVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

/**
 * 用户信息 DTO
 * 用于在用户登录成功后，返回用户的基本信息、角色、权限、菜单等数据
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户信息聚合结果")
public class UserInfoDTO {
    /**
     * 基础信息
     */
    @Schema(description = "基础个人信息")
    private SysUserVO user;
    /**
     * 角色标识列表 (如: ['admin'])
     */
    @Schema(description = "角色标识集合 (如: admin, hr_manager)")
    private Set<String> roles;
    /**
     * 权限码列表 (如: ['sys:user:add'])
     */
    @Schema(description = "权限标识符集合 (用于按钮级别权限控制，如: sys:user:add)")
    private Set<String> permissions;
    /**
     * 递归后的菜单树
     */
    @Schema(description = "动态菜单树 (用于左侧导航栏渲染及动态路由注入)")
    private List<MenuTreeVO> menus;
}
