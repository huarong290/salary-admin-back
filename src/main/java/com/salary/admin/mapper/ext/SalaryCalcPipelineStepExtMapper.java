package com.salary.admin.mapper.ext;

import com.salary.admin.mapper.auto.SalaryCalcPipelineStepMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 薪资计算管道步骤表 Mapper 接口
 * </p>
 *
 * 管道执行步骤：规则快照、阶段、顺序、执行控制等
 * 支持条件表达式、阻断策略、跳过策略等灵活配置
 *
 * @author system
 * @since 2026-03-27
 */
@Mapper
public interface SalaryCalcPipelineStepExtMapper extends SalaryCalcPipelineStepMapper {

    /**
     * 物理删除：直接删除记录
     */
    int physicalDeleteById(@Param("id") Long id);

    /**
     * 批量物理删除
     */
    int physicalDeleteByIds(@Param("ids") List<Long> ids);
}