package com.salary.admin.service.impl.salary;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.mapper.ext.SalaryConfigExtMapper;
import com.salary.admin.model.entity.salary.SalaryConfig;
import com.salary.admin.service.salary.ISalaryConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequiredArgsConstructor
public class SalaryConfigServiceImpl extends ServiceImpl<SalaryConfigExtMapper, SalaryConfig> implements ISalaryConfigService {

    private final SalaryConfigExtMapper salaryConfigExtMapper;
    // ======================== 1. 新增操作 (Create) ========================
    // ======================== 2. 删除操作 (Delete) ========================
    // ======================== 3. 修改操作 (Update) ========================
    // ======================== 4. 查询操作 (Read) ========================
}
