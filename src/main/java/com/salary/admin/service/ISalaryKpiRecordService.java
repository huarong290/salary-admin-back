package com.salary.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.salary.kpi.KpiBatchInitReqDTO;
import com.salary.admin.model.dto.salary.kpi.KpiEvaluateReqDTO;
import com.salary.admin.model.dto.salary.kpi.KpiRecordQueryReqDTO;
import com.salary.admin.model.entity.salary.SalaryKpiRecord;
import com.salary.admin.model.vo.salary.kpi.SalaryKpiRecordVO;

import java.util.List;

/**
 * <p>
 * 员工月度绩效考核记录表 服务接口
 * 负责绩效打分流转、引擎系数换算与周期强绑定
 * </p>
 *
 * @author system
 * @since 2026-04-04
 */
public interface ISalaryKpiRecordService extends IService<SalaryKpiRecord> {

    /**
     * 【核心查询】分页查询月度绩效考核大盘
     * 自动聚合员工姓名、部门等基础信息，供前端展示
     *
     * @param reqDTO 查询参数
     * @return 包含聚合信息的视图分页对象
     */
    PageResult<SalaryKpiRecordVO> getKpiRecordPage(KpiRecordQueryReqDTO reqDTO);

    /**
     * 【生命周期：1. 派发单据】批量初始化指定月份的绩效草稿单
     * 依赖 Period(考勤周期)，确保只为当月在职/有周期的员工生成绩效单，防止产生垃圾数据
     *
     * @param reqDTO 包含结算月份的请求参数
     */
    void initMonthlyKpi(KpiBatchInitReqDTO reqDTO);

    /**
     * 【生命周期：2. 业务打分】提交/修改绩效打分
     * 核心逻辑：根据主管提交的 A/B/C 评级，智能换算为算薪引擎所需的浮点系数
     *
     * @param reqDTO 打分请求参数
     */
    void evaluateKpi(KpiEvaluateReqDTO reqDTO);

    /**
     * 【生命周期：3. 审核定稿】定稿/确认绩效
     * 引擎抓取的前置条件，只有 audit_status = 1 的记录才会被算薪引擎提取
     *
     * @param ids 绩效记录主键列表
     */
    void confirmKpi(List<Long> ids);
}