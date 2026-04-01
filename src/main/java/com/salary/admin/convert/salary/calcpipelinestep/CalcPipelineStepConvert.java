package com.salary.admin.convert.salary.calcpipelinestep;


import com.salary.admin.model.dto.calcpipelinestep.CalcPipelineStepAddReqDTO;
import com.salary.admin.model.dto.calcpipelinestep.CalcPipelineStepEditReqDTO;
import com.salary.admin.model.entity.salary.SalaryCalcPipelineStep;
import com.salary.admin.model.vo.calcpipelinestep.CalcPipelineStepVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * 薪资计算管道步骤转换器
 *
 * 负责管道步骤表的双向映射：DTO -> Entity -> VO
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CalcPipelineStepConvert {

    /**
     * 新增请求 DTO 转换为数据库实体
     *
     * @param addDTO 前端传入的新增参数
     * @return 数据库实体对象
     */
    SalaryCalcPipelineStep toEntity(CalcPipelineStepAddReqDTO addDTO);

    /**
     * 修改请求 DTO 转换为数据库实体
     *
     * @param editDTO 前端传入的修改参数
     * @return 数据库实体对象
     */
    SalaryCalcPipelineStep toEntity(CalcPipelineStepEditReqDTO editDTO);

    /**
     * 实体对象转换为 VO
     *
     * @param entity 数据库实体
     * @return 视图对象
     */
    CalcPipelineStepVO toVO(SalaryCalcPipelineStep entity);

    /**
     * 批量转换实体列表为 VO 列表
     *
     * @param list 数据库实体列表
     * @return 视图对象列表
     */
    List<CalcPipelineStepVO> toVOList(List<SalaryCalcPipelineStep> list);
}
