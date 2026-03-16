package com.salary.admin.convert.salary.period;


import com.salary.admin.model.dto.salary.period.PeriodAddReqDTO;
import com.salary.admin.model.dto.salary.period.PeriodEditReqDTO;
import com.salary.admin.model.entity.salary.SalaryPeriod;
import com.salary.admin.model.vo.salary.period.PeriodOptionVO;
import com.salary.admin.model.vo.salary.period.PeriodVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * <p>
 * 薪资周期类型转换器
 * </p>
 *
 * @author system
 * @since 2026-03-11
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PeriodConvert {

    /**
     * 新增 DTO 转换为 薪资周期实体
     *
     * @param addDTO 新增请求对象
     * @return 薪资周期实体
     */
    SalaryPeriod toEntity(PeriodAddReqDTO addDTO);

    /**
     * 修改 DTO 转换为 薪资周期实体
     *
     * @param editDTO 修改请求对象
     * @return 薪资周期实体
     */
    SalaryPeriod toEntity(PeriodEditReqDTO editDTO);

    /**
     * 薪资周期实体 转换为 视图对象
     *
     * @param entity 薪资周期实体
     * @return 视图对象
     */
    PeriodVO toVO(SalaryPeriod entity);

    /**
     * 薪资周期实体列表 转换为 视图对象列表
     * 常用于分页查询结果转换
     *
     * @param list 实体列表
     * @return 视图对象列表
     */
    List<PeriodVO> toVOList(List<SalaryPeriod> list);

    /**
     * 实体映射为简易下拉选项
     * 将业务字段映射为通用的 label/value 结构
     */
    @Mapping(source = "workMonth", target = "label")
    @Mapping(source = "settlementMonth", target = "value")
    PeriodOptionVO toOptionVO(SalaryPeriod entity);

    /**
     * 实体列表批量映射为下拉选项列表
     */
    List<PeriodOptionVO> toOptionVOList(List<SalaryPeriod> list);


}