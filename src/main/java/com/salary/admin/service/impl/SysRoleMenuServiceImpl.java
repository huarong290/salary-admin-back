package com.salary.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.constants.redis.RedisCacheConstants;
import com.salary.admin.constants.role.RoleConstants;
import com.salary.admin.exception.BusinessException;
import com.salary.admin.mapper.ext.SysRoleMenuExtMapper;
import com.salary.admin.model.dto.rolemenu.RoleMenuAssignReqDTO;
import com.salary.admin.model.entity.sys.SysRole;
import com.salary.admin.model.entity.sys.SysRoleMenu;
import com.salary.admin.service.IRedisService;
import com.salary.admin.service.ISysRoleMenuService;
import com.salary.admin.service.ISysRoleService;
import com.salary.admin.service.ISysUserRoleService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 角色与菜单关联表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-02-22
 */
@Service
@Slf4j
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuExtMapper, SysRoleMenu> implements ISysRoleMenuService {
    @Resource
    private ISysRoleService iSysRoleService;
    @Resource
    private ISysUserRoleService iSysUserRoleService;
    @Resource
    private IRedisService iRedisService;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignMenusToRole(RoleMenuAssignReqDTO reqDTO) {
        Long roleId = reqDTO.getRoleId();
        List<Long> menuIds = reqDTO.getMenuIds();
        // 1. 获取角色详细信息（用于判断 Code）
        SysRole role = iSysRoleService.getById(roleId);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        // 🛡️ 终极防御：保护超级管理员的王座
        // 同时判断 ID 和 角色编码，确保天神账号的权限不被污染
        if (Long.valueOf(1L).equals(roleId) || RoleConstants.SUPER_ADMIN.equals(role.getRoleCode())) {
            throw new BusinessException("超级管理员拥有系统全量权限，无需手动分配，禁止修改其权限映射！");
        }

        // 2. 物理删除：清空该角色原有的所有菜单绑定关系
        LambdaQueryWrapper<SysRoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRoleMenu::getRoleId, roleId);
        this.remove(queryWrapper);

        // 3. 批量插入：如果前端传来的不是空数组，则重建关联
        if (menuIds != null && !menuIds.isEmpty()) {
            List<SysRoleMenu> roleMenuList = menuIds.stream().map(menuId -> {
                SysRoleMenu roleMenu = new SysRoleMenu();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menuId);
                return roleMenu;
            }).collect(Collectors.toList());

            this.saveBatch(roleMenuList);
        }
        // ⭐ 4. 核心联动逻辑：清理所有拥有该角色的用户缓存
        // 如果这个角色分配给了 100 个用户，这 100 个用户在 Redis 里的旧权限必须消失
        refreshUserPermissionsByRole(roleId);
        log.info("角色权限分配成功, roleId: {}, roleCode: {}, 分配的菜单IDs: {}",
                roleId, role.getRoleCode(), menuIds);
    }

    @Override
    public List<Long> getMenuIdsByRoleId(Long roleId) {
        // 返回纯粹的 ID 列表给前端
        LambdaQueryWrapper<SysRoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRoleMenu::getRoleId, roleId);
        queryWrapper.select(SysRoleMenu::getMenuId);

        return this.listObjs(queryWrapper, obj -> Long.valueOf(obj.toString()));
    }

    /**
     * 根据角色 ID 批量刷新用户权限缓存
     */
    private void refreshUserPermissionsByRole(Long roleId) {
        // 利用之前在 SysUserRoleServiceImpl 里写好的反查逻辑
        List<Long> userIds = iSysUserRoleService.getUserIdsByRoleId(roleId);

        if (userIds != null && !userIds.isEmpty()) {
            // 构造需要删除的所有 Redis Key
            List<String> cacheKeys = userIds.stream()
                    .map(uid -> RedisCacheConstants.AUTH_USER_AUTH + uid)
                    .collect(Collectors.toList());

            // 调用你 IRedisService 的批量删除功能
            iRedisService.del(cacheKeys);

            log.info("角色变更，已清理 {} 名受影响用户的权限缓存", userIds.size());
        }
    }
}
