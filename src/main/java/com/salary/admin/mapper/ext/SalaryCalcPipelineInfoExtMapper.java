package com.salary.admin.mapper.ext;

import com.salary.admin.mapper.auto.SalaryCalcPipelineInfoMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 薪资计算管道主表 Mapper 接口
 * </p>
 *
 * 管道元信息：编码、名称、版本、是否默认、状态等
 * 用于管理不同版本的薪资计算流程
 *
 * @author system
 * @since 2026-03-27
 */
@Mapper
public interface SalaryCalcPipelineInfoExtMapper extends SalaryCalcPipelineInfoMapper {

    /**
     * 物理删除：直接删除记录
     */
    int physicalDeleteById(@Param("id") Long id);

    /**
     * 批量物理删除
     */
    int physicalDeleteByIds(@Param("ids") List<Long> ids);
}
