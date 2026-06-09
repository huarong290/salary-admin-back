package com.salary.admin.mapper.ext;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.salary.admin.mapper.auto.SalaryAdjustmentMapper;
import com.salary.admin.model.dto.salary.adjustment.AdjustmentQueryDTO;
import com.salary.admin.model.vo.salary.adjustment.SalaryAdjustmentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 薪资周期专项调整表 (处理各类动态奖金与扣款) Mapper 扩展接口
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
@Mapper
public interface SalaryAdjustmentExtMapper  extends SalaryAdjustmentMapper {

    /**
     * 核心分页查询：关联 salary_employee 表获取员工姓名
     *
     * @param page     MyBatis-Plus 分页对象
     * @param queryDTO 查询条件 (含 employeeId, periodId, itemCode 等)
     * @return 封装了 VO 的分页结果
     */
    IPage<SalaryAdjustmentVO> selectAdjustmentPageVo(Page<SalaryAdjustmentVO> page, @Param("query") AdjustmentQueryDTO queryDTO);

    /**
     * 获取单条详细信息 (包含审计字段与员工姓名)
     */
    SalaryAdjustmentVO getDetailVoById(@Param("id") Long id);
}
