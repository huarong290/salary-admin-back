package com.salary.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.salary.admin.exception.BusinessException;
import com.salary.admin.mapper.ext.SysUserRoleExtMapper;
import com.salary.admin.model.dto.userrole.UserRoleAssignReqDTO;
import com.salary.admin.model.entity.sys.SysUserRole;
import com.salary.admin.mapper.auto.SysUserRoleMapper;
import com.salary.admin.service.ISysUserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户与角色关联表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-02-22
 */
@Service
@Slf4j
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleExtMapper, SysUserRole> implements ISysUserRoleService {


    /**
     * 给用户分配角色 (全量覆盖模式)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRolesToUser(UserRoleAssignReqDTO reqDTO) {
        Long userId = reqDTO.getUserId();
        List<Long> roleIds = reqDTO.getRoleIds();

        // 🛡️ 防御性编程：不允许通过此接口直接修改超级管理员(ID=1)的权限
        if (Long.valueOf(1L).equals(userId)) {
            throw new BusinessException("超级管理员的拥有至高权限，无需分配角色！");
        }

        // 1. 物理删除该用户原本拥有的所有角色绑定关系
        // 等同于 DELETE FROM sys_user_role WHERE user_id = ?
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getUserId, userId);
        this.remove(queryWrapper);


        // 2. 💡 重点：判断如果前端传的是非空数组，才执行批量插入
        if (!roleIds.isEmpty()) {
            List<SysUserRole> userRoleList = roleIds.stream().map(roleId -> {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                return userRole;
            }).collect(Collectors.toList());
        // 3. 批量插入新的关联关系 (利用 MyBatis-Plus 强大的批量保存能力)
            this.saveBatch(userRoleList);
        }


        log.info("用户角色分配成功, userId: {}, 分配的角色IDs: {}", userId, roleIds);
    }


}
