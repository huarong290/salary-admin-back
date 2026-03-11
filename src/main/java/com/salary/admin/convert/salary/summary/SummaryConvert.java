package com.salary.admin.convert.salary.summary;


import com.salary.admin.model.entity.salary.SalarySummary;
import com.salary.admin.model.vo.salary.summary.SummaryVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * 薪资汇总单转换器 (MapStruct)
 * 仅用于 Entity -> VO 的转换，因为汇总数据由系统自动生成，无 Add/Edit DTO
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SummaryConvert {

    /**
     * 实体转换为 VO
     */
    SummaryVO toVO(SalarySummary entity);

    /**
     * 实体列表转换为 VO 列表
     */
    List<SummaryVO> toVOList(List<SalarySummary> list);
}
