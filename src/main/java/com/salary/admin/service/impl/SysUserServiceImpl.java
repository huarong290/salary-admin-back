package com.salary.admin.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.salary.admin.common.PageResult;
import com.salary.admin.convert.menu.SysMenuConvert;
import com.salary.admin.convert.user.SysUserConvert;
import com.salary.admin.exception.BusinessException;
import com.salary.admin.mapper.ext.SysUserExtMapper;
import com.salary.admin.model.dto.user.UserAddReqDTO;
import com.salary.admin.model.dto.user.UserEditReqDTO;
import com.salary.admin.model.dto.user.UserInfoDTO;
import com.salary.admin.model.dto.user.UserQueryReqDTO;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private PasswordEncoder passwordEncoder; // 注入 Spring Security 的密码加密器
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

    /**
     * 分页查询用户列表
     *
     * @param reqDTO 查询参数
     * @return 分页结果
     */
    @Override
    public PageResult<SysUserVO> getUserPage(UserQueryReqDTO reqDTO) {
        // 1. 构造 MyBatis-Plus 分页对象
        Page<SysUser> pageParam = new Page<>(reqDTO.getPageNum(), reqDTO.getPageSize());

        // 2. 构造动态查询条件
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();

        // username 模糊查询 (LIKE)
        queryWrapper.like(StringUtils.isNotBlank(reqDTO.getUsername()), SysUser::getUsername, reqDTO.getUsername());

        // phone 精确查询 (EQ)
        queryWrapper.eq(StringUtils.isNotBlank(reqDTO.getPhone()), SysUser::getPhone, reqDTO.getPhone());

        // status 精确查询 (EQ)
        queryWrapper.eq(reqDTO.getStatus() != null, SysUser::getStatus, reqDTO.getStatus());

        // 按创建时间倒序排列 (最新的注册用户排前面)
        queryWrapper.orderByDesc(SysUser::getCreateTime);

        // 3. 执行物理分页查询
        IPage<SysUser> pageResult = this.page(pageParam, queryWrapper);

        // 4. 将 DO 实体列表转换为 VO 视图列表 (巧妙复用已注入的 sysUserConvert)
        List<SysUserVO> voList = pageResult.getRecords().stream()
                .map(sysUserConvert::toVO)
                .toList();

        // 5. 组装并返回统一分页结果
        return PageResult.of(pageResult, voList);
    }


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
        String rawPassword = StringUtils.isNotBlank(reqDTO.getPassword()) ? reqDTO.getPassword() : "123456";
        sysUser.setPassword(passwordEncoder.encode(rawPassword));

        // 5. 保存到数据库 (此时会触发之前写好的 MybatisPlusHandler，自动填充 createTime/createBy)
        sysUserExtMapper.insert(sysUser);

        // 6. 返回 MyBatis-Plus 自动回填的自增主键 ID
        return sysUser.getId();
    }

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
}
