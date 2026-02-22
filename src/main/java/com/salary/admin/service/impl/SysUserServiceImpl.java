package com.salary.admin.service.impl;

import com.salary.admin.mapper.ext.SysUserExtMapper;
import com.salary.admin.model.entity.sys.SysUser;
import com.salary.admin.mapper.auto.SysUserMapper;
import com.salary.admin.service.ISysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-02-22
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserExtMapper, SysUser> implements ISysUserService {

}
