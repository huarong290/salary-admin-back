package com.salary.admin.convert.user;

import com.salary.admin.model.dto.user.SysUserDTO;
import com.salary.admin.model.entity.sys.SysUser;
import com.salary.admin.model.vo.user.SysUserVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * 用户对象转换器 (MapStruct)
 * 职责：
 *  - 处理 SysUser 实体与 DTO、VO 之间的相互转换
 *  - 避免手写繁琐的 setter/getter，提高代码可维护性
 * 特性：
 *  - 使用 @Mapper(componentModel = "spring")，生成的实现类会由 Spring 管理
 *  - MapStruct 在编译期自动生成实现类 (SysUserConvertImpl)，无需手写
 *  - 支持集合类型的自动转换，内部会调用单个对象的转换方法
 * 注意：
 *  - 敏感字段（如密码）必须在转换时忽略，避免数据泄露
 *  - 后续可扩展更多方法，例如 Entity ↔ DTO 的双向转换
 */

@Mapper(componentModel = "spring")
public interface SysUserConvert {

    /**
     * 1. 表单对象转为数据库实体 (DTO -> Entity)
     * 用途：新增用户 (saveUser) 或更新用户 (updateUser) 时的入参转换
     *
     * @param dto 前端传入的表单数据
     * @return 转换后的数据库实体对象
     */
    SysUser toEntity(SysUserDTO dto);

    /**
     * 2. 数据库实体转为前端展示对象 (Entity -> VO)
     * 用途：查询用户详情 (getUserById) 等场景的返回值转换
     * 注意：出于安全考虑，必须在此处忽略密码字段的映射，防止敏感数据外泄
     *
     * @param entity 数据库查询出的实体对象
     * @return 过滤并转换后的视图层对象
     */
//    @Mapping(target = "password", ignore = true)
    SysUserVO toVO(SysUser entity);

    /**
     * 3. 实体列表转为前端展示对象列表 (List<Entity> -> List<VO>)
     * 用途：获取用户分页列表 (pageUser) 时的批量数据转换
     * 优势：MapStruct 内部会自动使用高效率的集合遍历，并复用 toVO 方法的安全规则
     *
     * @param entities 数据库实体集合
     * @return 转换后的视图层对象集合
     */
    List<SysUserVO> toVOList(List<SysUser> entities);

    /**
     * 4. 数据库实体转为表单对象 (Entity -> DTO)
     * 用途：编辑用户时回显表单数据 (editUser)，或在服务层进行数据传递
     * 注意：同样需要忽略密码字段，避免在表单中暴露敏感信息
     * @param entity 数据库查询出的实体对象
     * @return 转换后的表单对象，用于前端回显或服务层逻辑
     */
    @Mapping(target = "password", ignore = true)
    SysUserDTO toDTO(SysUser entity);

    /**
     * 5. 数据库实体列表转为表单对象列表 (List<Entity> -> List<DTO>)
     * 用途：批量转换用户数据，例如导出用户信息或批量编辑场景
     * 优势：MapStruct 会自动遍历集合并调用 toDTO 方法，保证安全规则一致
     * @param entities 数据库实体集合
     * @return 转换后的表单对象集合
     */
    List<SysUserDTO> toDTOList(List<SysUser> entities);
}
