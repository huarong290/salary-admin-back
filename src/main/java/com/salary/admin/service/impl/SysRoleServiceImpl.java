package com.salary.admin.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.salary.admin.common.PageResult;
import com.salary.admin.convert.role.SysRoleConvert;
import com.salary.admin.exception.BusinessException;
import com.salary.admin.mapper.ext.SysRoleExtMapper;
import com.salary.admin.mapper.ext.SysUserRoleExtMapper;
import com.salary.admin.model.dto.role.RoleAddReqDTO;
import com.salary.admin.model.dto.role.RoleEditReqDTO;
import com.salary.admin.model.dto.role.RoleQueryReqDTO;
import com.salary.admin.model.entity.sys.SysRole;
import com.salary.admin.mapper.auto.SysRoleMapper;
import com.salary.admin.model.vo.role.SysRoleVO;
import com.salary.admin.service.ISysRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    @Autowired
    private SysRoleConvert sysRoleConvert;

    // TODO: 如果你已经有了 SysUserRoleMapper，建议在这里注入，用于删除角色时的用户绑定校验
     @Autowired
     private SysUserRoleExtMapper sysUserRoleExtMapper;

// ======================== 1. 新增操作 (Create) ========================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addRole(RoleAddReqDTO reqDTO) {
        // 1. 唯一性校验：角色编码不能重复
        if (sysRoleExtMapper.checkRoleCodeUnique(reqDTO.getRoleCode(), null) != null) {
            throw new BusinessException("新增失败，角色编码已存在");
        }

        // 2. DTO 转 DO
        SysRole sysRole = sysRoleConvert.toDO(reqDTO);

        // 3. 调用 MyBatis-Plus 内置 save 方法，触发自动填充 (createTime, createBy 等)
        this.save(sysRole);

        return sysRole.getId();
    }


    // ======================== 2. 删除操作 (Delete) ========================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRoleById(Long id, boolean logicalDelete) {
        // 1. 核心安全防线：绝对不允许删除超级管理员角色 (假设固定 ID 为 1)
        if (Long.valueOf(1L).equals(id)) {
            throw new BusinessException("超级管理员角色不允许删除！");
        }

        // 2. 业务完整性校验：检查是否还有用户绑定了该角色 (防止产生脏数据)
        // 建议实现逻辑：select count(*) from sys_user_role where role_id = #{id}
        // if (sysUserRoleMapper.countByRoleId(id) > 0) {
        //     throw new BusinessException("该角色下已分配用户，请先解除绑定后再删除！");
        // }

        // 3. 路由分发
        if (logicalDelete) {
            return this.removeById(id);
        } else {
            return sysRoleExtMapper.physicalDeleteRoleById(id) > 0;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRoleByIds(List<Long> ids, boolean logicalDelete) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要删除的角色");
        }

        if (ids.contains(1L)) {
            throw new BusinessException("包含超级管理员角色，不允许删除！");
        }

        // 同理，批量删除也应该循环或使用 IN 语句检查是否有用户绑定
        // ...

        // 路由分发
        if (logicalDelete) {
            return this.removeByIds(ids);
        } else {
            return sysRoleExtMapper.batchPhysicalDeleteRoleByIds(ids) > 0;
        }
    }


    // ======================== 3. 修改操作 (Update) ========================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer editRole(RoleEditReqDTO reqDTO) {
        // 1. 检查角色是否存在
        SysRole oldRole = this.getById(reqDTO.getId());
        if (oldRole == null) {
            throw new BusinessException("修改失败，角色不存在");
        }

        // 2. 核心安全防线：如果试图修改超级管理员的编码，直接拦截
        if (Long.valueOf(1L).equals(reqDTO.getId()) && !StringUtils.equals("admin", reqDTO.getRoleCode())) {
            throw new BusinessException("超级管理员的权限编码不允许修改！");
        }

        // 3. 唯一性校验：修改后的角色编码不能与其他角色冲突
        if (sysRoleExtMapper.checkRoleCodeUnique(reqDTO.getRoleCode(), reqDTO.getId()) != null) {
            throw new BusinessException("修改失败，角色编码已被其他角色使用");
        }

        // 4. DTO 转 DO
        SysRole sysRole = sysRoleConvert.toDO(reqDTO);

        // 5. 调用 MyBatis-Plus 内置 updateById 方法，触发自动填充 (updateTime, updateBy 等)
        boolean success = this.updateById(sysRole);
        return success ? 1 : 0;
    }


    // ======================== 4. 查询操作 (Read) ========================

    @Override
    public PageResult<SysRoleVO> selectRoleListByPage(RoleQueryReqDTO reqDTO) {
        // 1. 初始化分页对象
        Page<SysRole> pageParam = new Page<>(reqDTO.getPageNum(), reqDTO.getPageSize());

        // 2. 调用 XML 中手写的动态查询 SQL
        Page<SysRole> pageResult = sysRoleExtMapper.selectRoleListByPage(pageParam, reqDTO);

        // 3. 将 DO 列表转换为 VO 视图列表 (利用 MapStruct 批量转换)
        List<SysRoleVO> voList = sysRoleConvert.toVOList(pageResult.getRecords());

        return PageResult.of(pageResult, voList);
    }

    @Override
    public List<SysRoleVO> selectAllNormalRoles() {
        // 1. 调用 XML 获取所有状态为 1 (正常) 且 delete_flag = 0 的角色
        List<SysRole> roleList = sysRoleExtMapper.selectAllNormalRoles();

        // 2. 转为 VO
        return sysRoleConvert.toVOList(roleList);
    }

    @Override
    public Set<String> selectRoleCodesByUserId(Long userId) {
        // 已经写好的核心跨表联查，直接复用
        return sysRoleExtMapper.selectRoleCodesByUserId(userId);
    }
}
