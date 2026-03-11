package com.salary.admin.service.salary;

import com.baomidou.mybatisplus.extension.service.IService;
import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.salary.employee.EmployeeAddReqDTO;
import com.salary.admin.model.dto.salary.employee.EmployeeEditReqDTO;
import com.salary.admin.model.dto.salary.employee.EmployeeQueryReqDTO;
import com.salary.admin.model.entity.salary.SalaryEmployee;
import com.salary.admin.model.vo.salary.employee.EmployeeVO;

import java.util.List;


/**
 * <p>
 * 员工基本信息表 服务类
 * </p>
 *
 * @author system
 * @since 2026-03-11
 */
public interface ISalaryEmployeeService extends IService<SalaryEmployee> {

    /**
     * 新增员工薪资档案
     *
     * @param reqDTO 新增请求对象
     * @return 新生成的员工ID
     */
    Long addEmployee(EmployeeAddReqDTO reqDTO);

    /**
     * 修改员工薪资档案
     *
     * @param reqDTO 修改请求对象
     * @return 修改结果
     */
    boolean editEmployee(EmployeeEditReqDTO reqDTO);

    /**
     * 分页查询员工薪资列表
     *
     * @param reqDTO 分页查询请求对象
     * @return 分页结果封装
     */
    PageResult<EmployeeVO> selectEmployeePage(EmployeeQueryReqDTO reqDTO);

    /**
     * 获取员工薪资档案详情
     *
     * @param id 员工ID
     * @return 员工视图对象
     */
    EmployeeVO getEmployeeDetail(Long id);

    /**
     * 删除员工档案 (逻辑/物理删除)
     *
     * @param id 员工主键 ID
     * @param logicalDelete 是否逻辑删除
     * true  = 逻辑删除（delete_flag = 1）
     * false = 物理删除（DELETE）
     * @return 是否删除成功
     */
    boolean deleteEmployeeById(Long id, boolean logicalDelete);

    /**
     * 批量删除员工档案 (逻辑/物理删除)
     *
     * @param ids 员工 ID 列表
     * @param logicalDelete 是否逻辑删除
     * true  = 逻辑删除（delete_flag = 1）
     * false = 物理删除（DELETE）
     * @return 是否成功
     */
    boolean deleteEmployeeByIds(List<Long> ids, boolean logicalDelete);
}
