package com.salary.admin.service.salary;

import com.salary.admin.model.entity.salary.SalaryPeriod;
import com.salary.admin.model.vo.salary.archive.SalaryArchiveVO;

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
     * 6. 🚀 引擎心脏：执行单人单周期规则管道计算，并生成明细与快照
     * @param summaryId 汇总单 ID
     * @param archive   员工生效的薪资档案快照
     * @param period    当期薪资周期/考勤数据
     * @return 汇总单 ID
     */
    Long createRecordByCalculation(Long summaryId, SalaryArchiveVO archive, SalaryPeriod period);


}