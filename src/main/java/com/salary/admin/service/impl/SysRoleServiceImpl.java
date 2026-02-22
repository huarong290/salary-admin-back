package com.salary.admin.service.impl;

import com.salary.admin.mapper.ext.SysRoleExtMapper;
import com.salary.admin.model.entity.sys.SysRole;
import com.salary.admin.mapper.auto.SysRoleMapper;
import com.salary.admin.service.ISysRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-02-22
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleExtMapper, SysRole> implements ISysRoleService {

}
