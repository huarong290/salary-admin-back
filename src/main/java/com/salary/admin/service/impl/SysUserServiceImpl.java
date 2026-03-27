package com.salary.admin.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.common.PageResult;
import com.salary.admin.constants.role.RoleConstants;
import com.salary.admin.convert.menu.SysMenuConvert;
import com.salary.admin.convert.user.SysUserConvert;
import com.salary.admin.exception.BusinessException;
import com.salary.admin.mapper.ext.SysUserExtMapper;
import com.salary.admin.model.dto.user.*;
import com.salary.admin.model.entity.sys.SysMenu;
import com.salary.admin.model.entity.sys.SysUser;
import com.salary.admin.model.vo.menu.MenuTreeVO;
import com.salary.admin.model.vo.user.SysUserVO;
import com.salary.admin.service.ISysMenuService;
import com.salary.admin.service.ISysRoleService;
import com.salary.admin.service.ISysUserService;
import com.salary.admin.utils.UserContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Autowired
    private PasswordEncoder passwordEncoder; // 注入 Spring Security 的密码加密器
    // ======================== 1. 新增操作 (Create) ========================

    /**
     * 新增用户
     *
     * @param reqDTO 新增参数
     * @return 新生成的主键 ID
     */
    @Override
    public Long addUser(UserAddReqDTO reqDTO) {
        // 1. 唯一性校验：用户名不能重复
        boolean existUsername = this.lambdaQuery()
                .eq(SysUser::getUsername, reqDTO.getUsername())
                .exists();
        if (existUsername) {
            throw new BusinessException("新增失败，登录账号已存在");
        }
        // 2. 唯一性校验：手机号不能重复 (如果前端传了的话)
        if (StringUtils.isNotBlank(reqDTO.getPhone())) {
            boolean existPhone = this.lambdaQuery()
                    .eq(SysUser::getPhone, reqDTO.getPhone())
                    .exists();
            if (existPhone) {
                throw new BusinessException("新增失败，手机号已存在");
            }
        }
        // 3. DTO 转 DO (复用 MapStruct)
        SysUser sysUser = sysUserConvert.toDO(reqDTO);
        // 4. 处理密码：如果没有传密码，默认设置初始密码为 "123456"
        String rawPassword = StringUtils.isNotBlank(reqDTO.getPassword().trim()) ? reqDTO.getPassword() : "123456";
        sysUser.setPassword(passwordEncoder.encode(rawPassword));
        // 5. 保存到数据库 (此时会触发之前写好的 MybatisPlusHandler，自动填充 createTime/createBy)
        sysUserExtMapper.insert(sysUser);
        // 6. 返回 MyBatis-Plus 自动回填的自增主键 ID
        return sysUser.getId();
    }
    // ======================== 2. 删除操作 (Delete) ========================

    /**
     * 删除用户 (逻辑删除)
     *
     * @param id            用户主键 ID
     * @param logicalDelete 是否逻辑删除
     *                      true  = 逻辑删除（delete_flag = 1）
     *                      false = 物理删除（DELETE）
     * @return 是否删除成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUserById(Long id, boolean logicalDelete) {
        SysUser sysUser = this.getById(id);
        if (sysUser == null) {
            throw new BusinessException("删除失败，用户不存在");
        }
        // 💡 路由分发：逻辑删除 vs 物理删除
        if (logicalDelete) {
            // 魔法发生的地方：
            // 只要实体类有 @TableLogic，下面这行代码就不会执行 DELETE FROM，
            // 而是自动被 MyBatis-Plus 替换成：UPDATE sys_user SET delete_flag = 1 WHERE id = ?
            // 💡 直接使用 Mapper 的 deleteById 触发 @TableLogic
            return sysUserExtMapper.deleteById(id) > 0;
        } else {
            return sysUserExtMapper.physicalDeleteUserById(id) > 0;
        }

    }

    /**
     * 删除用户 (逻辑删除)
     */
    @Override
    public boolean deleteUserByIds(List<Long> ids, boolean logicalDelete) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要删除的用户");
        }
        // 🛡️ 1. 绝对防御：禁止删除 ID 为 1 的系统初始管理员
        if (ids.contains(1L)) {
            throw new BusinessException("系统内置超级管理员(ID:1)是系统运行底座，严禁删除！");
        }
        // 🛡️ 2. 逻辑防御：禁止删除具有 SUPER_ADMIN 角色编码的用户
        // 即使 ID 不是 1，只要拥有超管角色，也不允许通过此接口直接删除（防止误删高权限账号）
        List<SysUser> users = this.listByIds(ids);
        for (Long userId : ids) {
            Set<String> roles = iSysRoleService.selectRoleCodesByUserId(userId);
            if (roles.contains(RoleConstants.SUPER_ADMIN)) {
                // 找到用户实体获取姓名，让报错更有针对性
                String username = users.stream()
                        .filter(u -> u.getId().equals(userId))
                        .map(SysUser::getUsername)
                        .findFirst().orElse("未知");
                throw new BusinessException("账号 [" + username + "] 拥有超级管理员权限，禁止删除！");
            }
        }
        //🛡️ 防御性编程3. 自我保护：防止用户把自己删了导致 Session 崩溃
        Long currentUserId = UserContextUtil.getUserId();
        if (ids.contains(currentUserId)) {
            throw new BusinessException("检测到当前登录账号在删除列表中，不能自杀式删除！");
        }
        // 💡 路由分发：批量逻辑删除 vs 批量物理删除
        if (logicalDelete) {
            // 💡 重点魔法：因为我们在 BaseEntity 的 deleteFlag 字段上加了 @TableLogic 注解
            // 所以底层执行的不是 DELETE FROM，而是 UPDATE sys_user SET delete_flag = 1 WHERE id IN (...)
            return sysUserExtMapper.deleteByIds(ids) > 0;
        } else {
            return sysUserExtMapper.batchPhysicalDeleteUserByIds(ids) > 0;
        }

    }
    // ======================== 3. 修改操作 (Update) ========================

    /**
     * 修改用户
     *
     * @param reqDTO 修改参数
     * @return 是否修改成功
     */
    @Override
    public Integer editUser(UserEditReqDTO reqDTO) {
        // 1. 检查要修改的用户是否存在
        SysUser oldUser = this.getById(reqDTO.getId());
        if (oldUser == null) {
            throw new BusinessException("修改失败，用户不存在");
        }
        // 2. 唯一性校验：手机号不能与其他人的重复
        if (StringUtils.isNotBlank(reqDTO.getPhone())) {
            boolean existPhone = this.lambdaQuery()
                    .eq(SysUser::getPhone, reqDTO.getPhone())
                    .ne(SysUser::getId, reqDTO.getId()) // 💡 重点：必须排除当前正在修改的用户自己
                    .exists();
            if (existPhone) {
                throw new BusinessException("修改失败，手机号已被其他用户使用");
            }
        }
        // 3. DTO 转 DO (复用 MapStruct)
        SysUser sysUser = sysUserConvert.toDO(reqDTO);
        // 4. 更新数据库 (此时会触发 MybatisPlusHandler，自动填充 updateTime/updateBy)
        // 💡 重点：直接返回 updateById 的 boolean 结果
        return sysUserExtMapper.updateById(sysUser);
    }

    /**
     * 重置密码
     */
    @Override
    public boolean resetUserPwd(UserResetPwdReqDTO reqDTO) {
        // 🛡️ 防御性编程：超级管理员的密码极其敏感，最好限制只能由他自己修改，不开放重置接口
        if (Long.valueOf(1L).equals(reqDTO.getId())) {
            throw new BusinessException("超级管理员密码不允许通过此接口重置！");
        }

        SysUser sysUser = this.getById(reqDTO.getId());
        if (sysUser == null) {
            throw new BusinessException("重置失败，用户不存在");
        }

        // 覆盖新密码 (使用 Spring Security 加密)
        // 如果没有传新密码，默认设置为 "123456"
        String rawPassword = StringUtils.isNotBlank(reqDTO.getPassword().trim()) ? reqDTO.getPassword() : "123456";
        sysUser.setPassword(passwordEncoder.encode(rawPassword));

        // 执行更新 (MybatisPlusHandler 会自动更新 updateTime 和 updateBy)
        return sysUserExtMapper.updateById(sysUser) > 0;
    }
    // ======================== 4. 查询操作 (Read) ========================

    /**
     * 分页查询用户列表
     *
     * @param reqDTO 查询参数
     * @return 分页结果
     */
    @Override
    public PageResult<SysUserVO> selectUserListByPage(UserQueryReqDTO reqDTO) {
        // 1. 构造 MyBatis-Plus 分页对象
        Page<SysUser> pageParam = new Page<>(reqDTO.getPageNum(), reqDTO.getPageSize());

        // 2. 调用 Mapper 的分页方法（XML 中定义了动态条件）
        Page<SysUser> pageResult = sysUserExtMapper.selectUserListByPage(pageParam, reqDTO);

        // 3. 将 DO 实体列表转换为 VO 视图列表
        List<SysUserVO> voList = pageResult.getRecords().stream()
                .map(sysUserConvert::toVO)
                .toList();

        // 4. 组装并返回统一分页结果
        return PageResult.of(pageResult, voList);
    }

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
    public UserInfoDTO selectUserInfoAggregation(Long userId) {
        // 1. 查询用户基础信息
        SysUser sysUser = sysUserExtMapper.selectById(userId);
        if (sysUser == null) {
            throw new BusinessException("当前登录用户不存在或已被删除");
        }
        // 2. 实体转 VO (利用刚刚写的 MapStruct 接口，自动忽略密码等敏感字段)
        SysUserVO userVO = sysUserConvert.toVO(sysUser);
        // 3. 调用 RoleService 获取角色集合
        Set<String> roles = iSysRoleService.selectRoleCodesByUserId(userId);
        // 4. 调用 MenuService 获取权限和菜单

        // 超级管理员：ID 为 1 或者 拥有 SUPER_ADMIN 编码
        boolean isSuperAdmin = Long.valueOf(1L).equals(userId) || roles.contains(RoleConstants.SUPER_ADMIN);
        // 普通管理员：拥有 ADMIN 编码但不是超管（或者你可以根据业务需求定义）
        boolean isNormalAdmin = roles.contains(RoleConstants.ADMIN);
        List<SysMenu> rawMenuList = new ArrayList<>();
        Set<String> permissions = new HashSet<>();
        if (isSuperAdmin) {
            // 【超管特权】直接从 sys_menu 表捞取所有数据，不走关联表
            rawMenuList = iSysMenuService.list();
            permissions = rawMenuList.stream()
                    .map(SysMenu::getMenuPermission)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toSet());
        } else {
            // 【普通管理员 & 其它角色】严谨地通过 sys_user_role -> sys_role_menu 关联表查询
            rawMenuList = iSysMenuService.selectMenuByUserId(userId);
            permissions = iSysMenuService.selectPermissionsByUserId(userId);
        }
        // 5. 构建树形结构
        List<MenuTreeVO> menuTree = iSysMenuService.buildMenuTree(rawMenuList);
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
