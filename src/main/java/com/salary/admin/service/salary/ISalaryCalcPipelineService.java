package com.salary.admin.service.salary;

import com.baomidou.mybatisplus.extension.service.IService;
import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.calcpipeline.CalcPipelineAddReqDTO;
import com.salary.admin.model.dto.calcpipeline.CalcPipelineEditReqDTO;
import com.salary.admin.model.dto.calcpipeline.CalcPipelineQueryReqDTO;
import com.salary.admin.model.entity.salary.SalaryCalcPipeline;
import com.salary.admin.model.vo.calcpipeline.CalcPipelineVO;

import java.util.List;

/**
 * <p>
 * 薪资计算流程管道表 服务类
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
public interface ISalaryCalcPipelineService extends IService<SalaryCalcPipeline> {
    // ======================== 1. 新增操作 (Create) ========================
    /**
     * 新增流程管道
     *
     * @param reqDTO 新增请求对象
     * @return 新生成的管道ID
     */
    Long addPipeline(CalcPipelineAddReqDTO reqDTO);
    // ======================== 2. 删除操作 (Delete) ========================
    /**
     * 删除流程管道 (逻辑/物理双模式)
     *
     * @param id 主键ID
     * @param logicalDelete 是否逻辑删除
     * @return 是否成功
     */
    boolean deletePipelineById(Long id, boolean logicalDelete);

    /**
     * 批量删除流程管道 (逻辑/物理双模式)
     *
     * @param ids 主键ID列表
     * @param logicalDelete 是否逻辑删除
     * @return 是否成功
     */
    boolean deletePipelineByIds(List<Long> ids, boolean logicalDelete);
    // ======================== 3. 修改操作 (Update) ========================
    /**
     * 修改流程管道
     *
     * @param reqDTO 修改请求对象
     * @return 修改结果
     */
    boolean editPipeline(CalcPipelineEditReqDTO reqDTO);
    // ======================== 4. 查询操作 (Read) ========================
    /**
     * 分页查询流程管道列表
     *
     * @param reqDTO 分页查询请求对象
     * @return 分页结果封装
     */
    PageResult<CalcPipelineVO> getPipelinePage(CalcPipelineQueryReqDTO reqDTO);

    /**
     * 获取流程管道详情
     *
     * @param id 管道主键ID
     * @return 管道视图对象
     */
    CalcPipelineVO getPipelineDetail(Long id);
}
