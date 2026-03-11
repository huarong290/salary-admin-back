package com.salary.admin.service.salary;

import com.baomidou.mybatisplus.extension.service.IService;
import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.salary.summary.SummaryCalcReqDTO;
import com.salary.admin.model.dto.salary.summary.SummaryQueryReqDTO;
import com.salary.admin.model.entity.salary.SalarySummary;
import com.salary.admin.model.vo.salary.summary.SummaryVO;

import java.util.List;

/**
 * 薪资结算汇总单 服务类
 */
public interface ISalarySummaryService extends IService<SalarySummary> {

    /**
     * 核心逻辑：触发结算计算 (根据流水自动汇总金额)
     * @param reqDTO 包含 periodId 的计算请求
     * @return 汇总单ID
     */
    Long calculateSummary(SummaryCalcReqDTO reqDTO);

    /** 分页查询薪资单 */
    PageResult<SummaryVO> selectSummaryPage(SummaryQueryReqDTO reqDTO);

    /** 详情查询 */
    SummaryVO getSummaryDetail(Long id);

    /** 删除单条结算单 (双模式) */
    boolean deleteById(Long id, boolean logicalDelete);

    /** 批量删除结算单 (双模式) */
    boolean deleteByIds(List<Long> ids, boolean logicalDelete);
}
