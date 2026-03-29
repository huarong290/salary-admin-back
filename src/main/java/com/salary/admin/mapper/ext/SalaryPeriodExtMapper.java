package com.salary.admin.mapper.ext;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.salary.admin.mapper.auto.SalaryPeriodMapper;
import com.salary.admin.model.dto.salary.period.PeriodQueryReqDTO;
import com.salary.admin.model.vo.salary.period.PeriodVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 薪资周期信息表 Mapper 扩展接口
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
@Mapper
public interface SalaryPeriodExtMapper extends SalaryPeriodMapper {
    /**
     * 分页查询薪资周期列表
     *
     * @param page   MyBatis-Plus 分页对象
     * @param reqDTO 查询过滤参数（包含员工关键字、结算月份等）
     * @return 薪资周期分页视图对象列表
     */
    IPage<PeriodVO> selectPeriodPage(Page<PeriodVO> page, @Param("req") PeriodQueryReqDTO reqDTO);
    /**
     * 物理删除（直接从数据库销毁记录）
     *
     * @param id 薪资周期主键ID
     * @return 成功删除的行数
     */
    int physicalDeleteById(@Param("id") Long id);

    /**
     * 批量物理删除（直接从数据库销毁多条记录）
     *
     * @param ids 薪资周期主键ID集合
     * @return 成功删除的行数
     */
    int physicalDeleteByIds(@Param("ids") List<Long> ids);
}
