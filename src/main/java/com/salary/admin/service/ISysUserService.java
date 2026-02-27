package com.salary.admin.service;

import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.user.*;
import com.salary.admin.model.entity.sys.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.salary.admin.model.vo.user.SysUserVO;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author system
 * @since 2026-02-22
 */
public interface ISysUserService extends IService<SysUser> {
    // ======================== 1. 新增操作 (Create) ========================

    /**
     * 新增用户
     *
     * @param reqDTO 新增用户请求参数
     * @return 新生成的主键 ID
     */
    Long addUser(UserAddReqDTO reqDTO);
    // ======================== 2. 删除操作 (Delete) ========================
    /**
     * 删除用户 (逻辑删除)
     *
     * @param id 用户主键 ID
     * @param logicalDelete 是否逻辑删除
     *                      true  = 逻辑删除（delete_flag = 1）
     *                      false = 物理删除（DELETE）
     * @return 是否删除成功
     */
    boolean deleteUserById(Long id, boolean logicalDelete);

    /**
     * 删除用户 (支持批量删除，多个 ID 用逗号隔开)
     *
     * @param ids 用户 ID 列表
     * @param logicalDelete 是否逻辑删除
     *                      true  = 逻辑删除（delete_flag = 1）
     *                      false = 物理删除（DELETE）
     * @return 是否成功
     */
    boolean deleteUserByIds(List<Long> ids, boolean logicalDelete);
    // ======================== 3. 修改操作 (Update) ========================

    /**
     * 修改用户
     *
     * @param reqDTO 编辑用户请求参数
     * @return 成功修改的记录数
     */
    Integer editUser(UserEditReqDTO reqDTO);

    /**
     * 重置用户密码
     *
     * @param reqDTO 重置密码参数
     * @return 是否成功
     */
    boolean resetUserPwd(UserResetPwdReqDTO reqDTO);
// ======================== 4. 查询操作 (Read) ========================
    /**
     * 分页查询用户列表 (支持多条件模糊查询)
     *
     * @param reqDTO 查询条件参数
     * @return 用户分页结果对象 (包含自动脱敏的 VO 列表)
     */
    PageResult<SysUserVO> selectUserListByPage(UserQueryReqDTO reqDTO);

    /**
     * 根据用户名获取用户基本信息 (内部调用/登录校验使用)
     *
     * @param username 用户名
     * @return 用户对象
     */
    SysUser selectUserByUsername(String username);

    /**
     * 根据用户id获取用户基本信息
     *
     * @param userId 用户ID
     * @return 用户信息聚合对象
     */
    UserInfoDTO selectUserInfoAggregation(Long userId);


}
