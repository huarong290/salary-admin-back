package com.salary.admin.mapper.ext.salary;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.salary.admin.mapper.auto.salary.SalaryDeductionDetailMapper;
import com.salary.admin.model.dto.salary.deductiondetail.DeductionDetailQueryReqDTO;
import com.salary.admin.model.vo.salary.deductiondetail.DeductionDetailVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 员工扣款明细表扩展 Mapper 接口
 * </p>
 *
 * @author system
 * @since 2026-03-11
 */
@Mapper
public interface SalaryDeductionDetailExtMapper extends SalaryDeductionDetailMapper {
    /**
     * 物理删除（直接删除记录）
     * @param id ID
     * @return 影响行数
     */
    int physicalDeleteById(@Param("id") Long id);

    /**
     * 批量物理删除
     * @param ids ID集合
     * @return 影响行数
     */
    int physicalDeleteByIds(@Param("ids") List<Long> ids);

    /**
     * 🌟 企业级改造：自定义多表关联分页查询扣款明细
     * * @param page MyBatis-Plus 的分页插件对象 (自动拦截拼装 LIMIT)
     * @param req  前端传来的多条件组合查询参数
     * @return 组装好各种外键名称的 VO 分页列表
     */
    Page<DeductionDetailVO> selectDeductionDetailByPage(Page<DeductionDetailVO> page, @Param("req") DeductionDetailQueryReqDTO req);
}