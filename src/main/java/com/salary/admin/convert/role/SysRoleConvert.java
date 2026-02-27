package com.salary.admin.convert.role;

import com.salary.admin.model.dto.role.RoleAddReqDTO;
import com.salary.admin.model.dto.role.RoleEditReqDTO;
import com.salary.admin.model.entity.sys.SysRole;
import com.salary.admin.model.vo.role.SysRoleVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * <p>
 * 角色对象类型转换器
 * </p>
 *
 * @author system
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SysRoleConvert {

    /**
     * DTO 转 DO (新增角色)
     *
     * @param reqDTO 新增参数
     * @return 实体对象
     */
    SysRole toDO(RoleAddReqDTO reqDTO);

    /**
     * DTO 转 DO (修改角色)
     *
     * @param reqDTO 修改参数
     * @return 实体对象
     */
    SysRole toDO(RoleEditReqDTO reqDTO);

    /**
     * DO 转 VO (单体转换)
     *
     * @param sysRole 实体对象
     * @return 视图对象
     */
    SysRoleVO toVO(SysRole sysRole);

    /**
     * DO 列表转 VO 列表 (批量转换，常用于下拉框或普通列表接口)
     *
     * @param roleList 实体对象列表
     * @return 视图对象列表
     */
    List<SysRoleVO> toVOList(List<SysRole> roleList);
}