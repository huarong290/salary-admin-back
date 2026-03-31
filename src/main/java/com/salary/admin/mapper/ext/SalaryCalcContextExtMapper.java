package com.salary.admin.mapper.ext;

import com.salary.admin.mapper.auto.SalaryCalcContextMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 薪资计算上下文快照表 Mapper 接口
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
@Mapper
public interface SalaryCalcContextExtMapper extends SalaryCalcContextMapper {
    /**
     * 物理删除：直接删除记录
     */
    int physicalDeleteById(@Param("id") Long id);

    /**
     * 批量物理删除
     */
    int physicalDeleteByIds(@Param("ids") List<Long> ids);
}
