package com.salary.admin.service.impl.salary;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.mapper.ext.SalaryArchiveExtMapper;
import com.salary.admin.model.entity.salary.SalaryArchive;
import com.salary.admin.service.salary.ISalaryArchiveService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 员工薪资标准配置表(含版本历史) 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
@Service
public class SalaryArchiveServiceImpl extends ServiceImpl<SalaryArchiveExtMapper, SalaryArchive> implements ISalaryArchiveService {

}
