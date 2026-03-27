package com.salary.admin.service.impl.salary;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.mapper.ext.SalaryCalcPipelineExtMapper;
import com.salary.admin.model.entity.salary.SalaryCalcPipeline;
import com.salary.admin.service.salary.ISalaryCalcPipelineService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 薪资计算流程管道表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
@Service
public class SalaryCalcPipelineServiceImpl extends ServiceImpl<SalaryCalcPipelineExtMapper, SalaryCalcPipeline> implements ISalaryCalcPipelineService {

}
