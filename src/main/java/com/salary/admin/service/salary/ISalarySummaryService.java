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

}
