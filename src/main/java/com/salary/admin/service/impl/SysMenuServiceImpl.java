package com.salary.admin.service.impl;

import com.salary.admin.mapper.ext.SysMenuExtMapper;
import com.salary.admin.model.entity.sys.SysMenu;
import com.salary.admin.mapper.auto.SysMenuMapper;
import com.salary.admin.service.ISysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 权限菜单表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-02-22
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuExtMapper, SysMenu> implements ISysMenuService {

}
