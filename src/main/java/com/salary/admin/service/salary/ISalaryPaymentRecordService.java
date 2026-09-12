package com.salary.admin.service.salary;

import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.salary.paymentrecord.PaymentRecordQueryReqDTO;
import com.salary.admin.model.entity.salary.SalaryPaymentRecord;
import com.salary.admin.model.vo.salary.paymentrecord.SalaryPaymentRecordVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 薪资结算明细/流水记录 服务类
 * </p>
 *
 * 业务定位：以 salary_summary 为主数据源，提供"工资单流水底稿"视图，
 * 支持按汇总批次、员工、月份筛选，以及财务手工修正实发金额。
 *
 * @author system
 * @since 2026-03-27
 */
public interface ISalaryPaymentRecordService extends IService<SalaryPaymentRecord> {

    /**
     * 分页查询工资单流水
     *
     * @param reqDTO 查询条件 (summaryId/keyword/month/isManual)
     * @return 分页流水视图
     */
    PageResult<SalaryPaymentRecordVO> pageQuery(PaymentRecordQueryReqDTO reqDTO);

    /**
     * 获取单条流水详情 (含解析后的明细快照)
     *
     * @param id salary_summary 主键
     * @return 流水视图
     */
    SalaryPaymentRecordVO getRecordDetail(Long id);

    /**
     * 财务手工修正实发金额
     * <p>
     * 联动逻辑：将 差额 (finalSalary - netSalary) 写入 salary_summary.manual_payment_amount，
     * 并追加审计备注，保证"实发 = 系统净额 + 手工调整"恒成立。
     *
     * @param id          汇总单 ID
     * @param finalSalary 修正后的最终实发金额
     * @param remark      调整原因 (审计必填)
     * @return 是否成功
     */
    boolean updateFinalSalary(Long id, java.math.BigDecimal finalSalary, String remark);

    /**
     * 逻辑删除工资单流水 (联动 salary_summary 逻辑删除)
     *
     * @param id 汇总单 ID
     * @return 是否成功
     */
    boolean deleteRecord(Long id);
}
