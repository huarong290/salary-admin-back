package com.salary.admin.convert.salary.deductiondetail;

import com.salary.admin.model.dto.salary.deductiondetail.DeductionDetailAddReqDTO;
import com.salary.admin.model.entity.salary.SalaryDeductionDetail;
import com.salary.admin.model.vo.deductiondetail.DeductionDetailVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * 扣款明细类型转换器
 *
 * 使用 MapStruct 框架实现 DTO、实体类、VO 之间的对象转换。
 *
 * - DTO（Data Transfer Object）：用于接收前端传入的数据
 * - Entity（实体类）：对应数据库表结构
 * - VO（View Object）：用于返回给前端展示的数据
 *
 * MapStruct 的优势：
 * 1. 自动生成对象转换代码，避免手写 setter/getter。
 * 2. 保持代码简洁，提高可维护性。
 * 3. 与 Spring 集成后可直接注入使用。
 */
@Mapper(
        componentModel = "spring", // 生成的实现类会被 Spring 容器管理，可直接注入使用
        unmappedTargetPolicy = ReportingPolicy.IGNORE // 忽略未映射的字段，避免报错
)
public interface DeductionDetailConvert {

    /**
     * 将新增请求 DTO 转换为实体对象
     * @param addDTO 新增请求参数
     * @return 转换后的 SalaryDeductionDetail 实体
     */
    SalaryDeductionDetail toEntity(DeductionDetailAddReqDTO addDTO);

    /**
     * 将实体对象转换为 VO，用于返回前端展示
     * @param entity 扣款明细实体
     * @return 转换后的 DeductionDetailVO
     */
    DeductionDetailVO toVO(SalaryDeductionDetail entity);

    /**
     * 将实体对象列表转换为 VO 列表
     * @param list 扣款明细实体列表
     * @return 转换后的 DeductionDetailVO 列表
     */
    List<DeductionDetailVO> toVOList(List<SalaryDeductionDetail> list);
}
