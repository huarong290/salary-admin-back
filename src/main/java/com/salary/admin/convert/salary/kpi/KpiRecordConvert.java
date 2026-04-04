package com.salary.admin.convert.salary.kpi;

import com.salary.admin.model.dto.salary.kpi.KpiEvaluateReqDTO;
import com.salary.admin.model.entity.salary.SalaryKpiRecord;
import com.salary.admin.model.vo.salary.kpi.SalaryKpiRecordVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * 员工月度绩效考核记录转换器
 * <p>
 * 处理绩效打分与视图展示的实体转换逻辑。
 * 注意：VO 中的 employeeName, employeeCode, departmentName 等跨表关联字段，
 * MapStruct 无法自动查库，需在 Service 层采用内存级联（防 N+1）的方式手动回填。
 *
 * @author system
 * @since 2026-04-04
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface KpiRecordConvert {

    /**
     * 用于主管打分：将打分 DTO 转换为实体（主要用于提取前台传入的评级与分数）
     *
     * @param req 绩效打分请求 DTO
     * @return 映射后的绩效实体
     */
    SalaryKpiRecord evaluateToEntity(KpiEvaluateReqDTO req);

    /**
     * 用于大盘展示：将核心实体转换为视图层 VO
     *
     * @param entity 绩效底层实体
     * @return 映射后的基础 VO
     */
    SalaryKpiRecordVO toVO(SalaryKpiRecord entity);

    /**
     * 批量转换：将实体列表转换为 VO 列表 (供分页查询使用)
     *
     * @param entities 绩效实体列表
     * @return 映射后的 VO 列表
     */
    List<SalaryKpiRecordVO> toVOList(List<SalaryKpiRecord> entities);
}