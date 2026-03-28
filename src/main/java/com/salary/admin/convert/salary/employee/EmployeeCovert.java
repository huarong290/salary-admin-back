package com.salary.admin.convert.salary.employee;

import com.salary.admin.model.dto.salary.employee.EmployeeAddReqDTO;
import com.salary.admin.model.dto.salary.employee.EmployeeEditReqDTO;
import com.salary.admin.model.entity.salary.SalaryEmployee;
import com.salary.admin.model.vo.salary.employee.EmployeeVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * <p>
 * employee类型转换器
 * </p>
 *
 * @author system
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EmployeeCovert {

    /**
     * AddDTO 转换为 Entity
     */
    SalaryEmployee toEntity(EmployeeAddReqDTO addDTO);

    /**
     * EditDTO 转换为 Entity
     */
    SalaryEmployee toEntity(EmployeeEditReqDTO editDTO);

    /**
     * Entity 转换为 VO
     * 💡 如果有字段名不一致，可以使用 @Mapping(source = "xxx", target = "yyy")
     */
    EmployeeVO toVO(SalaryEmployee entity);

    /**
     * Entity 列表转换为 VO 列表 (分页查询高频使用)
     */
    List<EmployeeVO> toVOList(List<SalaryEmployee> list);
}
