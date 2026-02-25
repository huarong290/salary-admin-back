package com.salary.admin.service.impl;

import com.salary.admin.mapper.ext.SysMenuExtMapper;
import com.salary.admin.model.entity.sys.SysMenu;
import com.salary.admin.mapper.auto.SysMenuMapper;
import com.salary.admin.service.ISysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 权限菜单表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-02-22
 */
@Service
@Slf4j
public class SysMenuServiceImpl extends ServiceImpl<SysMenuExtMapper, SysMenu> implements ISysMenuService {

    @Autowired
    private  SysMenuExtMapper sysMenuExtMapper;

    @Override
    public Set<String> selectPermissionsByUserId(Long userId) {
        return sysMenuExtMapper.selectPermissionsByUserId(userId);
    }

    @Override
    public List<SysMenu> selectMenuByUserId(Long userId) {
        return sysMenuExtMapper.selectMenuByUserId(userId);
    }
}
