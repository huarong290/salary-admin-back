package com.salary.admin.service.impl.salary;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.mapper.ext.SalaryPaymentRecordExtMapper;
import com.salary.admin.model.entity.salary.SalaryPaymentRecord;
import com.salary.admin.service.salary.ISalaryPaymentRecordService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 薪资结算明细记录表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
@Service
public class SalaryPaymentRecordServiceImpl extends ServiceImpl<SalaryPaymentRecordExtMapper, SalaryPaymentRecord> implements ISalaryPaymentRecordService {

}
