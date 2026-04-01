package com.salary.admin.convert.salary.calcpipeline;


import com.salary.admin.model.dto.calcpipeline.CalcPipelineAddReqDTO;
import com.salary.admin.model.dto.calcpipeline.CalcPipelineEditReqDTO;
import com.salary.admin.model.dto.calcpipeline.CalcPipelineItemDTO;
import com.salary.admin.model.entity.salary.SalaryCalcPipeline;
import com.salary.admin.model.vo.calcpipeline.CalcPipelineVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * 薪资计算流程管道转换器
 * 负责流程管道表的双向映射：DTO -> Entity -> VO。
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CalcPipelineConvert {

    /**
     * 新增请求 DTO 转换为数据库实体
     */
    SalaryCalcPipeline toEntity(CalcPipelineAddReqDTO addDTO);

    /**
     * 修改请求 DTO 转换为数据库实体
     */
    SalaryCalcPipeline toEntity(CalcPipelineEditReqDTO editDTO);

    /**
     * 实体对象转换为 VO
     */
    CalcPipelineVO toVO(SalaryCalcPipeline entity);

    /**
     * 批量转换实体列表为 VO 列表
     */
    List<CalcPipelineVO> toVOList(List<SalaryCalcPipeline> list);

    /**
     * 🌟 批量保存 DTO 转换为数据库实体
     */
    SalaryCalcPipeline toEntity(CalcPipelineItemDTO saveItemDTO);

    /**
     * 🌟 批量转换 DTO 列表为实体列表
     */
    List<SalaryCalcPipeline> toEntityList(List<CalcPipelineItemDTO> list);

}
