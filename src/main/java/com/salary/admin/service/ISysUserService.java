package com.salary.admin.service;

import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.user.UserAddReqDTO;
import com.salary.admin.model.dto.user.UserEditReqDTO;
import com.salary.admin.model.dto.user.UserInfoDTO;
import com.salary.admin.model.dto.user.UserQueryReqDTO;
import com.salary.admin.model.entity.sys.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.salary.admin.model.vo.user.SysUserVO;
import jakarta.validation.constraints.NotBlank;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author system
 * @since 2026-02-22
 */
public interface ISysUserService extends IService<SysUser> {
    /**
     * 根据用户名获取用户基本信息
     *
     * @param username 用户名
     * @return 用户对象
     */
    SysUser selectUserByUsername(String username);
    /**
     * 根据用户id获取用户基本信息
     *
     * @param userId 用户名
     * @return 用户信息聚合对象
     */
    UserInfoDTO getUserInfoAggregation(Long userId);
    /**
     * 分页查询用户列表 (支持多条件模糊查询)
     *
     * @param reqDTO 查询条件参数
     * @return 用户分页结果对象 (包含自动脱敏的 VO 列表)
     */
    PageResult<SysUserVO> getUserPage(UserQueryReqDTO reqDTO);
    /**
     * 新增用户
     * @param reqDTO 新增用户请求参数
     * @return 新生成的主键 ID
     */
    Long addUser(UserAddReqDTO reqDTO);

    /**
     * 修改用户
     *  @param reqDTO 编辑用户请求参数
     * @return 成功修改的记录数
     */
    Integer editUser(UserEditReqDTO reqDTO);
}
