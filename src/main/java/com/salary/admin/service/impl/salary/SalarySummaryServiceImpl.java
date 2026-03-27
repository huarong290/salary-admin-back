package com.salary.admin.service.impl.salary;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.mapper.ext.SalarySummaryExtMapper;
import com.salary.admin.model.entity.salary.SalarySummary;
import com.salary.admin.service.salary.ISalarySummaryService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 薪资汇总与结算表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
@Service
public class SalarySummaryServiceImpl extends ServiceImpl<SalarySummaryExtMapper, SalarySummary> implements ISalarySummaryService {

}
