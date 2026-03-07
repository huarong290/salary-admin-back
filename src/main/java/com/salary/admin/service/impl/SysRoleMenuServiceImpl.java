package com.salary.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.salary.admin.exception.BusinessException;
import com.salary.admin.mapper.ext.SysRoleMenuExtMapper;
import com.salary.admin.model.dto.rolemenu.RoleMenuAssignReqDTO;
import com.salary.admin.model.entity.sys.SysRoleMenu;
import com.salary.admin.mapper.auto.SysRoleMenuMapper;
import com.salary.admin.service.ISysRoleMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignMenusToRole(RoleMenuAssignReqDTO reqDTO) {
        Long roleId = reqDTO.getRoleId();
        List<Long> menuIds = reqDTO.getMenuIds();

        // 🛡️ 终极防御：保护超级管理员的王座
        if (Long.valueOf(1L).equals(roleId)) {
            throw new BusinessException("超级管理员拥有所有权限，禁止修改其权限映射！");
        }

        // 1. 物理删除：清空该角色原有的所有菜单绑定关系
        LambdaQueryWrapper<SysRoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRoleMenu::getRoleId, roleId);
        this.remove(queryWrapper);

        // 2. 批量插入：如果前端传来的不是空数组，则重建关联
        if (!menuIds.isEmpty()) {
            List<SysRoleMenu> roleMenuList = menuIds.stream().map(menuId -> {
                SysRoleMenu roleMenu = new SysRoleMenu();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menuId);
                return roleMenu;
            }).collect(Collectors.toList());

            this.saveBatch(roleMenuList);
        }

        log.info("角色权限分配成功, roleId: {}, 分配的菜单IDs: {}", roleId, menuIds);
    }

    @Override
    public List<Long> getMenuIdsByRoleId(Long roleId) {
        // 返回纯粹的 ID 列表给前端
        LambdaQueryWrapper<SysRoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRoleMenu::getRoleId, roleId);
        queryWrapper.select(SysRoleMenu::getMenuId);

        return this.listObjs(queryWrapper, obj -> Long.valueOf(obj.toString()));
    }
}
