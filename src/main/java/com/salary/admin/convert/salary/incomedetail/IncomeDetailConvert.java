package com.salary.admin.convert.salary.incomedetail;

import com.salary.admin.model.dto.salary.imcomedetail.IncomeDetailAddReqDTO;
import com.salary.admin.model.entity.salary.SalaryIncomeDetail;
import com.salary.admin.model.vo.salary.incomedetail.IncomeDetailVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * 收入明细转换器
 * 使用 MapStruct 框架实现 DTO、实体类、VO 之间的对象转换。
 *
 * - DTO（Data Transfer Object）：用于接收前端传入的数据
 * - Entity（实体类）：对应数据库表结构
 * - VO（View Object）：用于返回给前端展示的数据
 *
 * MapStruct 的好处是：
 * 1. 自动生成对象转换代码，避免手写冗余的 setter/getter。
 * 2. 保持代码简洁，提高可维护性。
 */
@Mapper(
        componentModel = "spring", // 生成的实现类会被 Spring 容器管理，可直接注入使用
        unmappedTargetPolicy = ReportingPolicy.IGNORE // 忽略未映射的字段，避免报错
)
public interface IncomeDetailConvert {

    /**
     * 将新增请求 DTO 转换为实体对象
     * @param addDTO 新增请求参数
     * @return 转换后的 SalaryIncomeDetail 实体
     */
    SalaryIncomeDetail toEntity(IncomeDetailAddReqDTO addDTO);

    /**
     * 将实体对象转换为 VO，用于返回前端展示
     * @param entity 收入明细实体
     * @return 转换后的 IncomeDetailVO
     */
    IncomeDetailVO toVO(SalaryIncomeDetail entity);

    /**
     * 将实体对象列表转换为 VO 列表
     * @param list 收入明细实体列表
     * @return 转换后的 IncomeDetailVO 列表
     */
    List<IncomeDetailVO> toVOList(List<SalaryIncomeDetail> list);
}

