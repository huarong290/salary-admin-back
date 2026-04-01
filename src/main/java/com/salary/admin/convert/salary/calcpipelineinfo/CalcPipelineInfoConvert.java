package com.salary.admin.convert.salary.calcpipelineinfo;


import com.salary.admin.model.dto.calcpipelineinfo.CalcPipelineInfoAddReqDTO;
import com.salary.admin.model.dto.calcpipelineinfo.CalcPipelineInfoEditReqDTO;
import com.salary.admin.model.entity.salary.SalaryCalcPipelineInfo;
import com.salary.admin.model.vo.calcpipelineinfo.CalcPipelineInfoVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * 薪资计算管道主表转换器
 *
 * 负责管道主表的双向映射：DTO -> Entity -> VO
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CalcPipelineInfoConvert {

    /**
     * 新增请求 DTO 转换为数据库实体
     *
     * @param addDTO 前端传入的新增参数
     * @return 数据库实体对象
     */
    SalaryCalcPipelineInfo toEntity(CalcPipelineInfoAddReqDTO addDTO);

    /**
     * 修改请求 DTO 转换为数据库实体
     *
     * @param editDTO 前端传入的修改参数
     * @return 数据库实体对象
     */
    SalaryCalcPipelineInfo toEntity(CalcPipelineInfoEditReqDTO editDTO);

    /**
     * 实体对象转换为 VO
     *
     * @param entity 数据库实体
     * @return 视图对象
     */
    CalcPipelineInfoVO toVO(SalaryCalcPipelineInfo entity);

    /**
     * 批量转换实体列表为 VO 列表
     *
     * @param list 数据库实体列表
     * @return 视图对象列表
     */
    List<CalcPipelineInfoVO> toVOList(List<SalaryCalcPipelineInfo> list);
}
