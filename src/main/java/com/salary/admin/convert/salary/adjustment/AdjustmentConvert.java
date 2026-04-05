package com.salary.admin.convert.salary.adjustment;

import com.salary.admin.model.dto.adjustment.AdjustmentAddReqDTO;
import com.salary.admin.model.dto.adjustment.AdjustmentEditReqDTO;
import com.salary.admin.model.entity.salary.SalaryAdjustment;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

/**
 * 薪资专项调整转换器
 * 处理专项调整（动态奖金/扣款）数据传输对象与实体间的核心转换逻辑。
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AdjustmentConvert {

    /**
     * 用于新增专项调整：将 Add DTO 转换为新的数据库实体
     */
    SalaryAdjustment addToEntity(AdjustmentAddReqDTO req);

    /**
     * 用于修改专项调整：将 Edit DTO 中的新值，覆盖到已查出的数据库老实体上
     * 💡 架构师技巧：@MappingTarget 可以避免我们手动去写几十个 set 方法
     */
    void updateEntity(AdjustmentEditReqDTO req, @MappingTarget SalaryAdjustment entity);

    // ---------------------------------------------------------
    // 如果你有对应的 VO 对象用于返回给前端，取消下方注释即可
    // ---------------------------------------------------------
//     SalaryAdjustmentVO toVO(SalaryAdjustment entity);
//     List<SalaryAdjustmentVO> toVOList(List<SalaryAdjustment> entities);
}