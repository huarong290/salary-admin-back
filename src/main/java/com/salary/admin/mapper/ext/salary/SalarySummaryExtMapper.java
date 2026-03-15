package com.salary.admin.mapper.ext.salary;

import com.salary.admin.mapper.auto.salary.SalarySummaryMapper;
import com.salary.admin.model.entity.salary.SalarySummary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 薪资汇总与结算表扩展 Mapper 接口
 * </p>
 *
 * @author system
 * @since 2026-03-11
 */
@Mapper
public interface SalarySummaryExtMapper extends SalarySummaryMapper {

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

    /**
     * 自定义批量插入方法
     */
    int batchInsert(@Param("list") List<SalarySummary> list);

    /**
     * 根据员工ID和结算月份查找汇总单ID
     * 用于核算引擎精准定位明细项的归属
     */
    Long findIdByEmployeeAndMonth(@Param("employeeId") Long employeeId, @Param("settlementMonth") String settlementMonth);
}
