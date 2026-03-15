package com.salary.admin.mapper.ext.salary;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.salary.admin.mapper.auto.salary.SalaryPaymentRecordMapper;
import com.salary.admin.model.dto.salary.paymentrecord.PaymentRecordQueryReqDTO;
import com.salary.admin.model.vo.salary.paymentrecord.SalaryPaymentRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 薪资结算明细记录表 Mapper 接口
 * </p>
 *
 * @author system
 * @since 2026-03-15
 */
@Mapper
public interface SalaryPaymentRecordExtMapper extends SalaryPaymentRecordMapper {


    /**
     * 联表分页查询：包含员工姓名、编号信息
     */
    Page<SalaryPaymentRecordVO> selectRecordPage(Page<SalaryPaymentRecordVO> page, @Param("req") PaymentRecordQueryReqDTO  req);
}
