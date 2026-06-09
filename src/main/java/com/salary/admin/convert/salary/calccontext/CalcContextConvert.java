package com.salary.admin.convert.salary.calccontext;


import com.salary.admin.model.dto.salary.calccontext.CalcContextAddReqDTO;
import com.salary.admin.model.dto.salary.calccontext.CalcContextEditReqDTO;
import com.salary.admin.model.entity.salary.SalaryCalcContext;
import com.salary.admin.model.vo.calccontext.CalcContextVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * 薪资计算上下文快照转换器
 * 负责上下文快照表的双向映射：DTO -> Entity -> VO。
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CalcContextConvert {

    /**
     * 新增请求 DTO 转换为数据库实体
     */
    SalaryCalcContext toEntity(CalcContextAddReqDTO addDTO);

    /**
     * 修改请求 DTO 转换为数据库实体
     */
    SalaryCalcContext toEntity(CalcContextEditReqDTO editDTO);

    /**
     * 实体对象转换为 VO
     */
    CalcContextVO toVO(SalaryCalcContext entity);

    /**
     * 批量转换实体列表为 VO 列表
     */
    List<CalcContextVO> toVOList(List<SalaryCalcContext> list);
}
