package com.salary.admin.service.impl.salary;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.mapper.ext.SalaryItemDetailExtMapper;
import com.salary.admin.model.entity.salary.SalaryItemDetail;
import com.salary.admin.service.salary.ISalaryItemDetailService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 统一收支明细表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
@Service
public class SalaryItemDetailServiceImpl extends ServiceImpl<SalaryItemDetailExtMapper, SalaryItemDetail> implements ISalaryItemDetailService {

}
