package com.salary.admin.service.salary;

import com.salary.admin.model.dto.engine.SalaryCalcBatchReqDTO;
import com.salary.admin.model.dto.engine.SalaryCalcSingleReqDTO;
import com.salary.admin.model.dto.salary.summary.SummaryInitReqDTO;
import com.salary.admin.model.vo.salary.summary.SalarySummaryVO;

/**
 * 薪资核心引擎接口 (V3.0 Pipeline 规则引擎版)
 * 负责处理跨模块业务逻辑编排，解除 Service 间的循环依赖。
 * 该引擎作为顶层调度者，协调 Archive(档案)、Pipeline(计算管道)、Detail(明细) 与 Summary(汇总) 的流转。
 *
 * @author system
 * @since 2026-03-31
 */
public interface ISalaryCoreEngine {

    /**
     * 【生命周期：1. 建账】初始化本月薪资账套
     * 自动对齐周期表并生成未核算的空壳汇总单据
     * @param reqDTO 包含结算月份和目标员工
     * @return 是否有新账套生成
     */
    boolean initSummaryAccount(SummaryInitReqDTO reqDTO);

    /**
     * 【生命周期：2. 预览】单人核算数据实时预览 (仅计算，不落库)
     * 供前端弹窗核对，防呆防错
     *
     * @param reqDTO 单人核算参数
     * @return 试算后的薪资快照视图
     */
    SalarySummaryVO previewCalculate(SalaryCalcSingleReqDTO reqDTO);

    /**
     * 【生命周期：3. 核算落盘】执行单人当月薪资核算 (瀑布流管道计算核心)
     *
     * @param reqDTO 单人核算参数
     */
    void calculateEmployeeSalary(SalaryCalcSingleReqDTO reqDTO);

    /**
     * 【生命周期：4. 批量跑批】批量执行薪资核算 (发薪台触发)
     *
     * @param reqDTO 批量核算参数
     */
    void calculateBatchSalary(SalaryCalcBatchReqDTO reqDTO);

}