package com.salary.admin.service.impl;

import com.salary.admin.mapper.ext.SysUserExtMapper;
import com.salary.admin.model.entity.sys.SysUser;
import com.salary.admin.mapper.auto.SysUserMapper;
import com.salary.admin.service.ISysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private SysUserExtMapper sysUserExtMapper;

    /**
     * 根据用户名查询系统用户
     * @param username 用户名
     * @return 实体对象
     */
    @Override
    public SysUser selectUserByUsername(String username) {
        //调用在 XML 中定义的自定义 SQL (适合复杂联查或特定优化)
        return sysUserExtMapper.selectUserByUsername(username);
    }
}
