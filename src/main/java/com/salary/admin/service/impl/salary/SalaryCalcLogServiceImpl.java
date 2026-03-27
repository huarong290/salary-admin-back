package com.salary.admin.service.impl.salary;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.mapper.ext.SalaryCalcLogExtMapper;
import com.salary.admin.model.entity.salary.SalaryCalcLog;
import com.salary.admin.service.salary.ISalaryCalcLogService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 薪资计算日志表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
@Service
public class SalaryCalcLogServiceImpl extends ServiceImpl<SalaryCalcLogExtMapper, SalaryCalcLog> implements ISalaryCalcLogService {

}
