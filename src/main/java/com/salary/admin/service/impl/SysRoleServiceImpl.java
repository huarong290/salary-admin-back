package com.salary.admin.service.impl;

import com.salary.admin.mapper.ext.SysRoleExtMapper;
import com.salary.admin.model.entity.sys.SysRole;
import com.salary.admin.mapper.auto.SysRoleMapper;
import com.salary.admin.service.ISysRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-02-22
 */
@Service
@Slf4j
public class SysRoleServiceImpl extends ServiceImpl<SysRoleExtMapper, SysRole> implements ISysRoleService {
    @Autowired
    private SysRoleExtMapper sysRoleExtMapper;

    @Override
    public Set<String> getRoleCodesByUserId(Long userId) {
        return sysRoleExtMapper.selectRoleCodesByUserId(userId);
    }
}
