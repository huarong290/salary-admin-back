package com.salary.admin.service.impl;

import com.salary.admin.mapper.ext.SysRoleMenuExtMapper;
import com.salary.admin.model.entity.sys.SysRoleMenu;
import com.salary.admin.mapper.auto.SysRoleMenuMapper;
import com.salary.admin.service.ISysRoleMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色与菜单关联表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-02-22
 */
@Service
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuExtMapper, SysRoleMenu> implements ISysRoleMenuService {

}
