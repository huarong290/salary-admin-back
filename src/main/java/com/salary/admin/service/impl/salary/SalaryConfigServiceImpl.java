package com.salary.admin.service.impl.salary;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.mapper.ext.SalaryConfigExtMapper;
import com.salary.admin.model.entity.salary.SalaryConfig;
import com.salary.admin.service.salary.ISalaryConfigService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 薪资系统全局配置表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
@Service
public class SalaryConfigServiceImpl extends ServiceImpl<SalaryConfigExtMapper, SalaryConfig> implements ISalaryConfigService {

}
