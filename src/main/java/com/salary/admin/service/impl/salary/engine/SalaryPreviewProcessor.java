package com.salary.admin.service.impl.salary.engine;

import com.salary.admin.model.dto.engine.SalaryCalcSingleReqDTO;
import com.salary.admin.model.dto.salary.snapshot.ArchiveSnapshot;
import com.salary.admin.model.entity.salary.SalarySummary;
import com.salary.admin.model.vo.salary.summary.SalarySummaryVO;
import com.salary.admin.service.salary.ISalarySummaryService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 预览核算处理器：纯内存试算，无事务，不落库
 */
@Service
public class SalaryPreviewProcessor extends AbstractSalaryProcessor<SalarySummaryVO> {

    @Resource
    private ISalarySummaryService summaryService;

    @Override
    protected SalarySummaryVO handleResult(SalaryCalcSingleReqDTO reqDTO, SalaryCalcAggregator aggregator, Map<String, Object> env) {
        SalarySummary summary = summaryService.getById(reqDTO.getSummaryId());

        // 遍历设置来源标识为预览模式
        aggregator.getAllItems().forEach(item -> item.setSource("SYSTEM_CALC_PREVIEW"));

        // 组装前端展示 VO
        SalarySummaryVO previewVO = new SalarySummaryVO();
        previewVO.setId(summary.getId());
        previewVO.setEmployeeName(summary.getEmployeeName());
        previewVO.setSettlementMonth(summary.getSettlementMonth());
        previewVO.setGrossSalary(aggregator.getIncomeTotal());
        previewVO.setDeductionTotal(aggregator.getDeductionTotal());
        previewVO.setTaxTotal(aggregator.getTaxTotal());
        previewVO.setNetSalary(aggregator.getNetSalary());
        previewVO.setManualPaymentAmount(summary.getManualPaymentAmount());

        // 注入溯源档案和全量明细快照
        previewVO.setUsedArchives((List<ArchiveSnapshot>) env.get("_usedArchives"));
        previewVO.setDetails(aggregator.getSnapshot());

        return previewVO;
    }
}
