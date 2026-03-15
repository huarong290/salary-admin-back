


package com.salary.admin.service.salary;

import com.baomidou.mybatisplus.extension.service.IService;
import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.salary.paymentrecord.PaymentRecordQueryReqDTO;
import com.salary.admin.model.entity.salary.SalaryPaymentRecord;
import com.salary.admin.model.vo.salary.archive.SalaryArchiveVO;
import com.salary.admin.model.vo.salary.paymentrecord.SalaryPaymentRecordVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 薪资结算明细记录表 服务类
 * 处理员工每月薪资核算的生成、手动调整及快照数据管理
 *
 * @author system
 * @since 2026-03-15
 */
public interface ISalaryPaymentRecordService extends IService<SalaryPaymentRecord> {

    /**
     * 分页查询结算明细
     * 包含员工基础信息联表查询及详情 JSON 解析
     *
     * @param queryReq 查询条件DTO
     * @return 分页VO数据
     */
    PageResult<SalaryPaymentRecordVO> selectRecordPage(PaymentRecordQueryReqDTO queryReq);

    /**
     * 系统核算生成记录
     * 根据薪资档案配置自动计算各项收入扣款并存入快照
     *
     * @param summaryId 汇总ID
     * @param archive   当前生效的薪资档案VO
     * @return 生成的记录ID
     */
    Long createByCalculation(Long summaryId, SalaryArchiveVO archive);

    /**
     * 手动录入总额生成记录
     * 用于只有最终薪资总额，没有详细核算项的特殊场景
     *
     * @param summaryId   汇总ID
     * @param employeeId  员工ID
     * @param finalAmount 最终总薪资
     * @param remark      录入备注
     * @return 生成的记录ID
     */
    Long createByManual(Long summaryId, Long employeeId, BigDecimal finalAmount, String remark);

    /**
     * 修改结算记录
     * 修改后需同步触发关联汇总表(salary_summary)的金额重新计算
     *
     * @param record 待修改的记录实体
     * @return 是否修改成功
     */
    boolean updateRecord(SalaryPaymentRecord record);

    /**
     * 删除结算记录
     * 删除后需同步触发关联汇总表(salary_summary)的金额递减
     *
     * @param id 记录ID
     * @return 是否删除成功
     */
    boolean removeRecord(Long id);

    /**
     * 批量删除结算记录
     *
     * @param ids 记录ID集合
     * @return 是否操作成功
     */
    boolean batchRemoveRecords(List<Long> ids);

    /**
     * 重新计算并刷新汇总表总额
     * 确保 summary 表中的 salary_total 等于所有 record 之和
     *
     * @param summaryId 汇总ID
     */
    void refreshSummaryAmount(Long summaryId);
}