package com.salary.admin.service.salary;

import com.salary.admin.model.entity.salary.SalaryPeriod;
import java.util.List;

/**
 * 薪资核心引擎接口
 * 负责处理跨模块业务逻辑编排，解除 Service 间的循环依赖
 */
public interface ISalaryCoreEngine {

    /**
     * 联动初始化：当薪资周期创建后，同步创建空的汇总记录
     * * @param periods 周期实体列表
     */
    void initSummaryForPeriods(List<SalaryPeriod> periods);

    /**
     * 重新计算并同步单条周期的汇总金额
     * 逻辑：实发 = 应发(收入明细累加) - 扣款(扣款明细累加)
     * * @param periodId 周期ID
     */
    void syncSummaryAmount(Long periodId);
}
