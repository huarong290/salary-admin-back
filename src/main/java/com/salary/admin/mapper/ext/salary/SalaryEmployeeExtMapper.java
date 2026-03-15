package com.salary.admin.mapper.ext.salary;

import com.salary.admin.mapper.auto.salary.SalaryEmployeeMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 员工基本信息表扩展 Mapper 接口
 * </p>
 *
 * @author system
 * @since 2026-03-11
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


    /**
     * 根据员工ID和结算月份查找汇总单ID
     * 用于核算引擎精准定位明细项的归属
     */
    Long findIdByEmployeeAndMonth(@Param("employeeId") Long employeeId, @Param("settlementMonth") String settlementMonth);
}
