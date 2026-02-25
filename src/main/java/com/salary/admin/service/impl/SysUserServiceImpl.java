package com.salary.admin.service.impl;

import com.alibaba.fastjson2.JSON;
import com.salary.admin.convert.menu.SysMenuConvert;
import com.salary.admin.convert.user.SysUserConvert;
import com.salary.admin.mapper.ext.SysUserExtMapper;
import com.salary.admin.model.dto.user.UserInfoDTO;
import com.salary.admin.model.entity.sys.SysMenu;
import com.salary.admin.model.entity.sys.SysUser;
import com.salary.admin.mapper.auto.SysUserMapper;
import com.salary.admin.model.vo.menu.MenuTreeVO;
import com.salary.admin.model.vo.user.SysUserVO;
import com.salary.admin.service.ISysMenuService;
import com.salary.admin.service.ISysRoleService;
import com.salary.admin.service.ISysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-02-22
 */
@Service
@Slf4j
public class SysUserServiceImpl extends ServiceImpl<SysUserExtMapper, SysUser> implements ISysUserService {
    @Autowired
    private SysUserExtMapper sysUserExtMapper;
    @Autowired
    private ISysMenuService iSysMenuService;
    @Autowired
    private ISysRoleService iSysRoleService;
    @Autowired
    private SysUserConvert sysUserConvert;

    @Autowired
    private SysMenuConvert sysMenuConvert;
    /**
     * 根据用户名查询系统用户
     *
     * @param username 用户名
     * @return 实体对象
     */
    @Override
    public SysUser selectUserByUsername(String username) {
        //调用在XML中定义的自定义 SQL (适合复杂联查或特定优化)
        return sysUserExtMapper.selectUserByUsername(username);
    }

    /**
     * 获取当前登录用户的聚合信息 (包含基本信息、角色、权限、动态路由菜单)
     * * @param userId 当前登录用户 ID
     *
     * @return UserInfoDTO 聚合数据传输对象
     */
    @Override
    public UserInfoDTO getUserInfoAggregation(Long userId) {
        // 1. 查询用户基础信息
        SysUser sysUser = sysUserExtMapper.selectById(userId);
        if (sysUser == null) {
            throw new RuntimeException("当前登录用户不存在或已被删除");
        }
        // 2. 实体转 VO (利用刚刚写的 MapStruct 接口，自动忽略密码等敏感字段)
        SysUserVO userVO = sysUserConvert.toVO(sysUser);

        // 3. 调用 RoleService 获取角色集合
        Set<String> roles = iSysRoleService.getRoleCodesByUserId(userId);
        // 4. 调用 MenuService 获取权限和菜单
        Set<String> permissions = iSysMenuService.selectPermissionsByUserId(userId);
        List<SysMenu> rawMenuList = iSysMenuService.selectMenuByUserId(userId);

        // 5. 构建树形结构
        List<MenuTreeVO> menuTree = sysMenuConvert.buildMenuTree(rawMenuList);


        // 6. 组装返回
        UserInfoDTO userInfoDTO = UserInfoDTO.builder()
                .user(userVO)
                .roles(roles)
                .permissions(permissions)
                .menus(menuTree)
                .build();
        log.info("用户聚合信息装配完成, userId=[{}],userInfoDTO:[{}]", userId, JSON.toJSONString(userInfoDTO));
        return userInfoDTO;
    }
}
