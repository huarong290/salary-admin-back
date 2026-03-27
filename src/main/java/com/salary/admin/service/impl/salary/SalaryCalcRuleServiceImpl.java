package com.salary.admin.service.impl.salary;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.mapper.ext.SalaryCalcRuleExtMapper;
import com.salary.admin.model.entity.salary.SalaryCalcRule;
import com.salary.admin.service.salary.ISalaryCalcRuleService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 薪资计算规则库表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
@Service
public class SalaryCalcRuleServiceImpl extends ServiceImpl<SalaryCalcRuleExtMapper, SalaryCalcRule> implements ISalaryCalcRuleService {

}
