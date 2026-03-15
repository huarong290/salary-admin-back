package com.salary.admin.convert.salary.deductiontype;


import com.salary.admin.model.dto.salary.deductiontype.DeductionTypeAddReqDTO;
import com.salary.admin.model.dto.salary.deductiontype.DeductionTypeEditReqDTO;
import com.salary.admin.model.entity.salary.SalaryDeductionType;
import com.salary.admin.model.vo.salary.deductiontype.DeductionTypeOptionVO;
import com.salary.admin.model.vo.salary.deductiontype.DeductionTypeVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * 扣款类型转换器
 * 使用 MapStruct 框架实现 DTO、实体类、VO 之间的对象转换。
 *
 * - DTO（Data Transfer Object）：用于接收前端传入的数据
 * - Entity（实体类）：对应数据库表结构
 * - VO（View Object）：用于返回给前端展示的数据
 */
@Mapper(
        componentModel = "spring", // 生成的实现类会被 Spring 管理，可直接注入使用
        unmappedTargetPolicy = ReportingPolicy.IGNORE // 忽略未映射的字段，避免报错
)
public interface DeductionTypeConvert {

    /**
     * 将新增请求 DTO 转换为实体对象
     * @param addDTO 新增请求参数
     * @return 转换后的 SalaryDeductionType 实体
     */
    SalaryDeductionType toEntity(DeductionTypeAddReqDTO addDTO);

    /**
     * 将编辑请求 DTO 转换为实体对象
     * @param editDTO 编辑请求参数
     * @return 转换后的 SalaryDeductionType 实体
     */
    SalaryDeductionType toEntity(DeductionTypeEditReqDTO editDTO);

    /**
     * 将实体对象转换为 VO，用于返回前端展示
     * @param entity 扣款类型实体
     * @return 转换后的 DeductionTypeVO
     */
    DeductionTypeVO toVO(SalaryDeductionType entity);

    /**
     * 将实体对象列表转换为 VO 列表
     * @param list 扣款类型实体列表
     * @return 转换后的 DeductionTypeVO 列表
     */
    List<DeductionTypeVO> toVOList(List<SalaryDeductionType> list);

    // 🌟 --- 新增下拉选项转换方法 --- 🌟

    /**
     * 将实体对象转换为下拉选项 VO
     * @param entity 扣款类型实体
     * @return DeductionTypeOptionVO
     */
    DeductionTypeOptionVO toOptionVO(SalaryDeductionType entity);

    /**
     * 将实体对象列表转换为下拉选项 VO 列表
     * @param list 实体列表
     * @return 下拉选项 VO 列表
     */
    List<DeductionTypeOptionVO> toOptionVOList(List<SalaryDeductionType> list);
}

