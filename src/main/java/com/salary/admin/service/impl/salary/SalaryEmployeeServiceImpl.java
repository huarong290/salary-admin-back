package com.salary.admin.service.impl.salary;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.mapper.ext.SalaryEmployeeExtMapper;
import com.salary.admin.model.entity.salary.SalaryEmployee;
import com.salary.admin.service.salary.ISalaryEmployeeService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 员工基本信息表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
@Service
public class SalaryEmployeeServiceImpl extends ServiceImpl<SalaryEmployeeExtMapper, SalaryEmployee> implements ISalaryEmployeeService {

}
