package com.salary.admin.service.salary;

import com.salary.admin.model.dto.engine.SalaryCalcBatchReqDTO;
import com.salary.admin.model.dto.engine.SalaryCalcSingleReqDTO;

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
     * 执行单人当月薪资核算 (瀑布流管道计算核心)
     *
     * @param reqDTO 单人核算参数
     */
    void calculateEmployeeSalary(SalaryCalcSingleReqDTO reqDTO);

    /**
     * 批量执行薪资核算 (发薪台触发)
     *
     * @param reqDTO 批量核算参数
     */
    void calculateBatchSalary(SalaryCalcBatchReqDTO reqDTO);



}