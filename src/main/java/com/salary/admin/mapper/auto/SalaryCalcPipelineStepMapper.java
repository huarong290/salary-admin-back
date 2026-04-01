package com.salary.admin.mapper.auto;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.salary.admin.model.entity.salary.SalaryCalcPipelineStep;

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
public interface SalaryCalcPipelineStepMapper extends BaseMapper<SalaryCalcPipelineStep> {

}