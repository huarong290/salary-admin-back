package com.salary.admin.service.impl.salary;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.mapper.ext.salary.SalaryCategoryExtMapper;
import com.salary.admin.model.entity.salary.SalaryCategory;
import com.salary.admin.service.salary.ISalaryCategoryService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 薪资档案固定项明细表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-03-13
 */
@Service
public class SalaryCategoryServiceImpl  extends ServiceImpl<SalaryCategoryExtMapper, SalaryCategory> implements ISalaryCategoryService {
}
