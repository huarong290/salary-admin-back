package com.salary.admin.service.salary;

import com.baomidou.mybatisplus.extension.service.IService;
import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.salary.imcometype.IncomeTypeAddReqDTO;
import com.salary.admin.model.dto.salary.imcometype.IncomeTypeEditReqDTO;
import com.salary.admin.model.dto.salary.imcometype.IncomeTypeQueryReqDTO;
import com.salary.admin.model.entity.salary.SalaryIncomeType;
import com.salary.admin.model.vo.salary.incometype.IncomeTypeOptionVO;
import com.salary.admin.model.vo.salary.incometype.IncomeTypeVO;

import java.util.List;

/**
 * <p>
 * 收入类型字典表 服务类
 * </p>
 *
 * @author system
 * @since 2026-03-11
 */
public interface ISalaryIncomeTypeService extends IService<SalaryIncomeType> {

    /**
     * 新增收入类型
     *
     * @param reqDTO 新增请求参数
     * @return 新生成的类型ID
     */
    Long addIncomeType(IncomeTypeAddReqDTO reqDTO);

    /**
     * 修改收入类型
     *
     * @param reqDTO 修改请求参数
     * @return 修改结果
     */
    boolean editIncomeType(IncomeTypeEditReqDTO reqDTO);

    /**
     * 分页查询收入类型列表
     *
     * @param reqDTO 分页查询参数
     * @return 统一分页结果
     */
    PageResult<IncomeTypeVO> selectIncomeTypePage(IncomeTypeQueryReqDTO reqDTO);

    /**
     * 获取收入类型详情
     *
     * @param id 收入类型ID
     * @return 视图对象
     */
    IncomeTypeVO getIncomeTypeDetail(Long id);

    /**
     * 删除收入类型 (双模式)
     *
     * @param id            主键ID
     * @param logicalDelete 是否逻辑删除 (true: 逻辑, false: 物理)
     * @return 是否成功
     */
    boolean deleteById(Long id, boolean logicalDelete);

    /**
     * 批量删除收入类型 (双模式)
     *
     * @param ids           主键ID列表
     * @param logicalDelete 是否逻辑删除
     * @return 是否成功
     */
    boolean deleteByIds(List<Long> ids, boolean logicalDelete);

    /**
     * 获取收入类型下拉列表 (用于定薪/调薪表单)
     * * @return 包含ID和名称的简易对象列表
     */
    List<IncomeTypeOptionVO> listIncomeTypeOptions();
}
