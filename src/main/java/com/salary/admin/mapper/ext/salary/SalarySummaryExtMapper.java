package com.salary.admin.mapper.ext.salary;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.salary.admin.mapper.auto.salary.SalarySummaryMapper;
import com.salary.admin.model.dto.salary.summary.SummaryQueryReqDTO;
import com.salary.admin.model.entity.salary.SalarySummary;
import com.salary.admin.model.vo.salary.summary.SummaryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 薪资汇总与结算表扩展 Mapper 接口
 * 负责处理涉及多表关联、物理操作及复杂聚合的薪资业务查询
 * </p>
 *
 * @author system
 * @since 2026-03-11
 */
@Mapper
public interface SalarySummaryExtMapper extends SalarySummaryMapper {

    /**
     * 物理删除（数据清理使用，非业务逻辑删除）
     * * @param id 汇总单主键ID
     * @return 影响行数
     */
    int physicalDeleteById(@Param("id") Long id);

    /**
     * 批量物理删除
     * * @param ids ID集合
     * @return 影响行数
     */
    int physicalDeleteByIds(@Param("ids") List<Long> ids);

    /**
     * 高性能批量插入
     * * @param list 待插入的汇总单实体列表
     * @return 插入成功的行数
     */
    int batchInsert(@Param("list") List<SalarySummary> list);

    /**
     * 根据员工ID和结算月份精准查找汇总单ID
     * 核心逻辑：通过 SalaryPeriod 表关联锁定对应的 Summary 记录
     * * @param employeeId 员工ID
     * @param settlementMonth 结算月份 (格式: YYYYMM)
     * @return 汇总单ID (若不存在则返回 null)
     */
    Long findIdByEmployeeAndMonth(@Param("employeeId") Long employeeId, @Param("settlementMonth") String settlementMonth);

    /**
     * 分页查询薪资汇总列表 (多表关联优化版)
     * 关联 SalaryPeriod 和 SalaryEmployee 表，一次性取出所有视图字段
     * * @param page 分页拦截器参数
     * @param req  包含员工ID、月份等维度的查询请求DTO
     * @return 包含 VO 数据的分页对象
     */
    IPage<SummaryVO> selectSummaryPageVo(Page<SummaryVO> page, @Param("req") SummaryQueryReqDTO req);

    /**
     * 根据汇总单ID获取详情 (多表关联版)
     * 一次性带出员工姓名、结算月份等视图字段
     * * @param id 汇总单ID
     * @return 填充完整的 SummaryVO
     */
    SummaryVO selectSummaryVoById(@Param("id") Long id);
}