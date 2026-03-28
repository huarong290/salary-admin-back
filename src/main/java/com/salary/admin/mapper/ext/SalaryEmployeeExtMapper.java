package com.salary.admin.mapper.ext;

import com.salary.admin.mapper.auto.SalaryEmployeeMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 员工基本信息表扩展 Mapper 接口
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
@Mapper
public interface SalaryEmployeeExtMapper extends SalaryEmployeeMapper {

    /**
     * 物理删除：直接删除记录
     */
    int physicalDeleteById(@Param("id") Long id);

    /**
     * 批量物理删除
     */
    int physicalDeleteByIds(@Param("ids") List<Long> ids);

}
