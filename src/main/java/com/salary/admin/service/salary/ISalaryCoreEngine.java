package com.salary.admin.service.salary;

import com.salary.admin.model.dto.salary.period.PeriodAddReqDTO;
import com.salary.admin.model.dto.salary.period.PeriodBatchInitReqDTO;
import com.salary.admin.model.entity.salary.SalaryPeriod;
import com.salary.admin.model.vo.salary.archive.SalaryArchiveVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 薪资核心引擎接口
 * 负责处理跨模块业务逻辑编排，解除 Service 间的循环依赖。
 * 该引擎作为顶层调度者，协调 Archive(档案)、Record(明细) 与 Summary(汇总) 的流转。
 * * @author system
 * @since 2026-03-15
 */
public interface ISalaryCoreEngine {

    /**
     * 1. 批量初始化薪资周期 (从 PeriodService 迁移)
     * 逻辑：根据选择的员工自动生成周期记录，并同步调用 initSummaryForPeriods 生成汇总记录
     */
    boolean batchInitPeriods(PeriodBatchInitReqDTO reqDTO);

    /**
     * 2. 联动初始化：当薪资周期创建后，同步创建空的汇总记录
     */
    void initSummaryForPeriods(List<SalaryPeriod> periods);

    /**
     * 3. 系统核算生成记录 (从 PaymentRecordService 迁移)
     * 逻辑：根据薪资档案配置自动计算各项收入扣款并存入快照
     */
    Long createRecordByCalculation(Long summaryId, SalaryArchiveVO archive, SalaryPeriod period);

    /**
     * 4. 手动录入总额生成记录 (从 PaymentRecordService 迁移)
     */
    Long createRecordByManual(Long summaryId, Long employeeId, BigDecimal finalAmount, String remark);

    /**
     * 5. 全局核算任务：一键生成指定月份所有在职员工的薪资结算明细
     * 逻辑：抓取档案 -> 清理旧账 -> 生成明细(快照) -> 更新汇总总额
     */
    void executeGlobalSettlement(String settlementMonth);

    /**
     * 6. 重新计算并同步单条周期的汇总金额 (兼容通过 summaryId 或 periodId 刷新)
     * 逻辑：实发 = 应发(收入合计) - 扣款(扣款合计)
     */
    void syncSummaryAmountByPeriodId(Long periodId);

    /**
     * 7. 根据汇总单ID直接刷新汇总金额
     * * @param summaryId 汇总单ID
     */
    void refreshSummaryAmountBySummaryId(Long summaryId);

    /**
     * 8. 新增单条薪资周期并同步创建汇总单
     * 逻辑：调用基础 PeriodService 保存单条周期记录，成功后联动生成对应的 Summary 空记录
     * * @param reqDTO 单条周期新增请求参数
     * @return 新生成的薪资周期ID
     */
    Long addPeriodAndSummary(PeriodAddReqDTO reqDTO);
}



