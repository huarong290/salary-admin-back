package com.salary.admin.service.impl.salary;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.mapper.ext.SalaryItemConfigExtMapper;
import com.salary.admin.model.entity.salary.SalaryItemConfig;
import com.salary.admin.service.salary.ISalaryItemConfigService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 薪资项目统一配置表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
@Service
public class SalaryItemConfigServiceImpl extends ServiceImpl<SalaryItemConfigExtMapper, SalaryItemConfig> implements ISalaryItemConfigService {

}
