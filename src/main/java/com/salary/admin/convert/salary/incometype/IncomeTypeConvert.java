package com.salary.admin.convert.salary.incometype;

import com.salary.admin.model.dto.salary.imcometype.IncomeTypeAddReqDTO;
import com.salary.admin.model.dto.salary.imcometype.IncomeTypeEditReqDTO;
import com.salary.admin.model.entity.salary.SalaryIncomeType;
import com.salary.admin.model.vo.salary.incometype.IncomeTypeVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * 收入类型类型转换器
 *
 * @author system
 * @since 2026-03-11
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IncomeTypeConvert {

    /**
     * 新增 DTO 转换为实体
     */
    SalaryIncomeType toEntity(IncomeTypeAddReqDTO addDTO);

    /**
     * 修改 DTO 转换为实体
     */
    SalaryIncomeType toEntity(IncomeTypeEditReqDTO editDTO);

    /**
     * 实体转换为 VO
     */
    IncomeTypeVO toVO(SalaryIncomeType entity);

    /**
     * 实体列表转换为 VO 列表
     */
    List<IncomeTypeVO> toVOList(List<SalaryIncomeType> list);
}