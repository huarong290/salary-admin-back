package com.salary.admin.convert.salary.calcrule;

import com.salary.admin.model.dto.salary.calcrule.CalcRuleAddReqDTO;
import com.salary.admin.model.dto.salary.calcrule.CalcRuleEditReqDTO;
import com.salary.admin.model.entity.salary.SalaryCalcRule;
import com.salary.admin.model.vo.calcrule.CalcRuleOptionVO;
import com.salary.admin.model.vo.calcrule.CalcRuleVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * 薪资计算规则转换器
 * 负责规则库表的双向映射：DTO -> Entity -> VO。
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CalcRuleConvert {

    /**
     * 新增请求 DTO 转换为数据库实体
     */
    SalaryCalcRule toEntity(CalcRuleAddReqDTO addDTO);

    /**
     * 修改请求 DTO 转换为数据库实体
     */
    SalaryCalcRule toEntity(CalcRuleEditReqDTO editDTO);

    /**
     * 实体对象转换为 VO
     */
    CalcRuleVO toVO(SalaryCalcRule entity);

    /**
     * 批量转换实体列表为 VO 列表
     */
    List<CalcRuleVO> toVOList(List<SalaryCalcRule> list);

    /**
     * 实体对象转换为下拉选项 VO
     */
    CalcRuleOptionVO toOptionVO(SalaryCalcRule entity);

    /**
     * 批量转换实体列表为下拉选项 VO 列表
     */
    List<CalcRuleOptionVO> toOptionVOList(List<SalaryCalcRule> list);
}
