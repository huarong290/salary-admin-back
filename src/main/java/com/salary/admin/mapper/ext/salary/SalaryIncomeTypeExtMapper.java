package com.salary.admin.mapper.ext.salary;

import com.salary.admin.mapper.auto.salary.SalaryIncomeTypeMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 收入类型字典表扩展 Mapper 接口
 * </p>
 *
 * @author system
 * @since 2026-03-11
 */
@Mapper
public interface SalaryIncomeTypeExtMapper extends SalaryIncomeTypeMapper {

    /**
     * 物理删除（直接删除记录）
     * @param id ID
     * @return 影响行数
     */
    int physicalDeleteById(@Param("id") Long id);

    /**
     * 批量物理删除
     * @param ids ID集合
     * @return 影响行数
     */
    int physicalDeleteByIds(@Param("ids") List<Long> ids);
}
