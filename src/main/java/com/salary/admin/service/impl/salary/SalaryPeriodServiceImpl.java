package com.salary.admin.service.impl.salary;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.mapper.ext.SalaryPeriodExtMapper;
import com.salary.admin.model.entity.salary.SalaryPeriod;
import com.salary.admin.service.salary.ISalaryPeriodService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 薪资周期信息表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
@Service
public class SalaryPeriodServiceImpl extends ServiceImpl<SalaryPeriodExtMapper, SalaryPeriod> implements ISalaryPeriodService {

}
