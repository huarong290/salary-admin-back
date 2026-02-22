package com.salary.admin.service.impl;

import com.salary.admin.mapper.ext.SysUserRoleExtMapper;
import com.salary.admin.model.entity.sys.SysUserRole;
import com.salary.admin.mapper.auto.SysUserRoleMapper;
import com.salary.admin.service.ISysUserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户与角色关联表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-02-22
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleExtMapper, SysUserRole> implements ISysUserRoleService {

}
