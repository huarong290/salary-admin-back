package com.salary.admin.service.salary;

import com.baomidou.mybatisplus.extension.service.IService;
import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.salary.summary.SalarySummaryOperateDTO;
import com.salary.admin.model.dto.salary.summary.SummaryQueryReqDTO;
import com.salary.admin.model.entity.salary.SalarySummary;
import com.salary.admin.model.vo.salary.summary.SalarySummaryVO;

/**
 * <p>
 * 薪资汇总与结算表 服务类
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
public interface ISalarySummaryService extends IService<SalarySummary> {


    /**
     * 分页查询视图对象 (处理 JSON 解析)
     */
    PageResult<SalarySummaryVO> getSummaryPage(SummaryQueryReqDTO queryDTO);

    /**
     * 获取单个工资单详情
     */
    SalarySummaryVO getSummaryDetail(Long id);
    /**
     * 锁定薪资单
     */
    /** 统一更新锁定状态 (支持批量/单个) */
    boolean updateLockStatus(SalarySummaryOperateDTO operateDTO);


    /**
     * 🌟 [引擎专用] 根据周期和员工获取唯一汇总单据
     * 用于计算引擎在核算完成后，将结果回写到主表
     *
     * @param periodId   薪资周期ID
     * @param employeeId 员工ID
     * @return 薪资汇总实体
     */
    SalarySummary getSummaryByUnique(Long periodId, Long employeeId);

}
