package com.salary.admin.service.salary;

import com.baomidou.mybatisplus.extension.service.IService;
import com.salary.admin.model.dto.calccontext.CalcContextAddReqDTO;
import com.salary.admin.model.dto.calccontext.CalcContextEditReqDTO;
import com.salary.admin.model.dto.calccontext.CalcContextQueryReqDTO;
import com.salary.admin.model.entity.salary.SalaryCalcContext;
import com.salary.admin.model.vo.calccontext.CalcContextVO;

import java.util.List;

/**
 * <p>
 * 薪资计算上下文快照表 服务类
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
public interface ISalaryCalcContextService extends IService<SalaryCalcContext> {
    // ======================== 1. 新增操作 (Create) ========================
    /**
     * 新增上下文快照
     *
     * @param reqDTO 新增请求对象
     * @return 新生成的快照ID
     */
    Long addContext(CalcContextAddReqDTO reqDTO);
    // ======================== 2. 删除操作 (Delete) ========================
    /**
     * 删除上下文快照 (逻辑/物理双模式)
     *
     * @param id 主键ID
     * @param logicalDelete 是否逻辑删除
     * @return 是否成功
     */
    boolean deleteContextById(Long id, boolean logicalDelete);

    /**
     * 批量删除上下文快照 (逻辑/物理双模式)
     *
     * @param ids 主键ID列表
     * @param logicalDelete 是否逻辑删除
     * @return 是否成功
     */
    boolean deleteContextByIds(List<Long> ids, boolean logicalDelete);
    // ======================== 3. 修改操作 (Update) ========================
    /**
     * 修改上下文快照
     *
     * @param reqDTO 修改请求对象
     * @return 修改结果
     */
    boolean editContext(CalcContextEditReqDTO reqDTO);
    // ======================== 4. 查询操作 (Read) ========================
    /**
     * 查询上下文快照列表
     *
     * @param reqDTO 查询请求对象
     * @return 快照列表
     */
    List<CalcContextVO> listContext(CalcContextQueryReqDTO reqDTO);

    /**
     * 获取上下文快照详情
     *
     * @param id 快照主键ID
     * @return 快照视图对象
     */
    CalcContextVO getContextDetail(Long id);
}
