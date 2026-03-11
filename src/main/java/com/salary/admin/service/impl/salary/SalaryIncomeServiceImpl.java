package com.salary.admin.service.impl.salary;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.mapper.ext.salary.SalaryIncomeExtMapper;
import com.salary.admin.model.entity.salary.SalaryIncome;
import com.salary.admin.service.salary.ISalaryIncomeService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 员工收入主表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-03-11
 */
@Service
public class SalaryIncomeServiceImpl extends ServiceImpl<SalaryIncomeExtMapper, SalaryIncome> implements ISalaryIncomeService {

}
