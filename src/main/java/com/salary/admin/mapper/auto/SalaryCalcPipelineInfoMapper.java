package com.salary.admin.mapper.auto;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.salary.admin.model.entity.salary.SalaryCalcPipelineInfo;

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
public interface SalaryCalcPipelineInfoMapper extends BaseMapper<SalaryCalcPipelineInfo> {

}
