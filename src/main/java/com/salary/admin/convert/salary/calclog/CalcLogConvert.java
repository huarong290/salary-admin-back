package com.salary.admin.convert.salary.calclog;


import com.salary.admin.model.entity.salary.SalaryCalcLog;
import com.salary.admin.model.vo.calclog.CalcLogVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * 薪资计算日志转换器
 * 负责日志表的单向映射：Entity -> VO。
 * 日志通常只展示，不需要新增/修改 DTO 转换。
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CalcLogConvert {

    /**
     * 实体对象转换为 VO
     */
    CalcLogVO toVO(SalaryCalcLog entity);

    /**
     * 批量转换实体列表为 VO 列表
     */
    List<CalcLogVO> toVOList(List<SalaryCalcLog> list);
}
