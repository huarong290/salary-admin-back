package com.salary.admin.convert.salary.adjustment;

import com.salary.admin.model.dto.adjustment.AdjustmentAddReqDTO;
import com.salary.admin.model.dto.adjustment.AdjustmentEditReqDTO;
import com.salary.admin.model.entity.salary.SalaryAdjustment;
import com.salary.admin.model.vo.salary.adjustment.SalaryAdjustmentVO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * 薪资专项调整转换器
 * <p>
 * 核心职责：处理 DTO、Entity、VO 之间的互转，解耦数据库模型与前端视图。
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AdjustmentConvert {

    /**
     * 【新增专用】将 Add DTO 转换为新的数据库实体
     */
    SalaryAdjustment addToEntity(AdjustmentAddReqDTO req);

    /**
     * 【修改专用】将 Edit DTO 中的新值，覆盖到已从 DB 查出的老实体上
     * 💡 架构师技巧：通过 @MappingTarget 自动完成属性对齐，无需手动写大量 set 方法。
     */
    void updateEntity(AdjustmentEditReqDTO req, @MappingTarget SalaryAdjustment entity);

    /**
     * 【展示专用】将实体转换为前端展示用的视图对象 (VO)
     */
    SalaryAdjustmentVO toVO(SalaryAdjustment entity);

    /**
     * 【展示专用】批量转换列表数据
     */
    List<SalaryAdjustmentVO> toVOList(List<SalaryAdjustment> entities);
}