package com.salary.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.constants.redis.RedisCacheConstants;
import com.salary.admin.exception.BusinessException;
import com.salary.admin.mapper.ext.SysUserRoleExtMapper;
import com.salary.admin.model.dto.userrole.UserRoleAssignReqDTO;
import com.salary.admin.model.entity.sys.SysUserRole;
import com.salary.admin.service.IRedisService;
import com.salary.admin.service.ISysUserRoleService;
import jakarta.annotation.Resource;
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

    @Resource
    private SysUserRoleExtMapper sysUserRoleExtMapper;

    @Resource
    private IRedisService iRedisService;
    /**
     * 给用户分配角色 (全量覆盖模式)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRolesToUser(UserRoleAssignReqDTO reqDTO) {
        Long userId = reqDTO.getUserId();
        List<Long> roleIds = reqDTO.getRoleIds();

        // 🛡️ 1. 绝对防御：禁止修改 ID 为 1 的用户
        if (Long.valueOf(1L).equals(userId)) {
            throw new BusinessException("系统内置超级管理员权限受保护，禁止通过此接口修改！");
        }

        // 2. 物理删除原有关联
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getUserId, userId);
        this.remove(queryWrapper);

        // 3. 批量插入新关联
        if (roleIds != null && !roleIds.isEmpty()) {
            List<SysUserRole> userRoleList = roleIds.stream().map(roleId -> {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                return userRole;
            }).collect(Collectors.toList());
            this.saveBatch(userRoleList);
        }

        // ⭐ 4. 联动逻辑：权限实时生效
        // 清理受影响用户的 Redis 权限缓存，下次该用户操作时，JwtFilter 会自动重新加载最新角色和菜单
        clearUserAuthCache(userId);

        log.info("用户角色分配成功, userId: {}, 分配的角色IDs: {}", userId, roleIds);
    }


    @Override
    public List<Long> getRoleIdsByUserId(Long userId) {
        return this.listObjs(
                new LambdaQueryWrapper<SysUserRole>()
                        .select(SysUserRole::getRoleId)
                        .eq(SysUserRole::getUserId, userId),
                obj -> Long.valueOf(obj.toString())
        );
    }

    @Override
    public List<Long> getUserIdsByRoleId(Long roleId) {
        return sysUserRoleExtMapper.selectUserIdsByRoleId(roleId);
    }

    /**
     * ⭐ 抽取清理缓存方法，便于后续复用
     */
    private void clearUserAuthCache(Long userId) {
        try {
            String cacheKey = RedisCacheConstants.AUTH_USER_AUTH + userId;
            iRedisService.del(cacheKey);
            log.info("已清理用户 ID: {} 的权限缓存，权限将实时刷新", userId);
        } catch (Exception e) {
            // 缓存清理失败不应该导致业务事务回滚，但需要记录警告
            log.warn("权限缓存清理失败，userId: {}, 原因: {}", userId, e.getMessage());
        }
    }
}
