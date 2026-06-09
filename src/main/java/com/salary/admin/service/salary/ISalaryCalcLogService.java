package com.salary.admin.service.salary;

import com.baomidou.mybatisplus.extension.service.IService;
import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.salary.calclog.CalcLogQueryReqDTO;
import com.salary.admin.model.entity.salary.SalaryCalcLog;
import com.salary.admin.model.vo.calclog.CalcLogVO;

import java.util.List;

/**
 * <p>
 * 薪资计算日志表 服务类
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
public interface ISalaryCalcLogService extends IService<SalaryCalcLog> {
    // ======================== 1. 新增操作 (Create) ========================
    // ======================== 2. 删除操作 (Delete) ========================
    /**
     * 删除日志 (逻辑/物理双模式)
     *
     * @param id 主键ID
     * @param logicalDelete 是否逻辑删除
     * @return 是否成功
     */
    boolean deleteLogById(Long id, boolean logicalDelete);

    /**
     * 批量删除日志 (逻辑/物理双模式)
     *
     * @param ids 主键ID列表
     * @param logicalDelete 是否逻辑删除
     * @return 是否成功
     */
    boolean deleteLogByIds(List<Long> ids, boolean logicalDelete);
    // ======================== 3. 修改操作 (Update) ========================
    // ======================== 4. 查询操作 (Read) ========================

    /**
     * 分页查询日志列表
     *
     * @param reqDTO 分页查询请求对象
     * @return 分页结果封装
     */
    PageResult<CalcLogVO> selectLogPage(CalcLogQueryReqDTO reqDTO);

    /**
     * 获取日志详情
     *
     * @param id 日志主键ID
     * @return 日志视图对象
     */
    CalcLogVO getLogDetail(Long id);
    /**
     * 查询指定员工在某周期的日志列表
     *
     * @param employeeId 员工ID
     * @param periodId 薪资周期ID
     * @return 日志列表
     */
    List<CalcLogVO> listLogsByEmployee(Long employeeId, Long periodId);
}
