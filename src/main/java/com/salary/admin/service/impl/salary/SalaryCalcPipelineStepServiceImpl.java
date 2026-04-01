package com.salary.admin.service.impl.salary;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.mapper.ext.SalaryCalcPipelineStepExtMapper;
import com.salary.admin.model.entity.salary.SalaryCalcPipelineStep;
import com.salary.admin.service.salary.ISalaryCalcPipelineStepService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 薪资计算管道步骤表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SalaryCalcPipelineStepServiceImpl extends ServiceImpl<SalaryCalcPipelineStepExtMapper, SalaryCalcPipelineStep> implements ISalaryCalcPipelineStepService {
}
