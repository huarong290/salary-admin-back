package com.salary.admin.service.salary;

import com.baomidou.mybatisplus.extension.service.IService;
import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.salary.calcpipelineinfo.CalcPipelineInfoAddReqDTO;
import com.salary.admin.model.dto.salary.calcpipelineinfo.CalcPipelineInfoEditReqDTO;
import com.salary.admin.model.dto.salary.calcpipelineinfo.CalcPipelineInfoQueryReqDTO;
import com.salary.admin.model.entity.salary.SalaryCalcPipelineInfo;
import com.salary.admin.model.vo.calcpipelineinfo.CalcPipelineInfoVO;

/**
 * <p>
 * 薪资计算流程管道主表 服务类
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
public interface ISalaryCalcPipelineInfoService extends IService<SalaryCalcPipelineInfo> {

    // ======================== 1. 新增操作 (Add) ========================
    /**
     * 新增管道信息
     * @param addDTO 新增参数DTO
     * @return 是否成功
     */
    boolean addPipeline(CalcPipelineInfoAddReqDTO addDTO);
    // ======================== 2. 删除操作 (Delete) ========================

    // ======================== 3. 修改操作 (Update) ========================
    /**
     * 修改管道信息
     * @param editDTO 修改参数DTO
     * @return 是否成功
     */
    boolean updatePipeline(CalcPipelineInfoEditReqDTO editDTO);
    /**
     * 设置某个管道为“默认流程”
     * 业务逻辑：全局只能有一个默认管道，需将其他管道的 default_flag 置为 0
     * @param id 管道的主键ID
     * @return 是否成功
     */
    boolean setDefaultPipeline(Long id);
    /**
     * 复制现有管道并生成新版本 (用于年度调薪规则变更)
     * 业务逻辑：复制 info 表生成 version+1，并全量深拷贝关联的 step 表明细
     * @param sourceId 源管道ID
     * @return 新生成的管道ID
     */
    Long copyAndUpgradePipeline(Long sourceId);
    // ======================== 4. 查询操作 (Query) ========================
    /**
     * 分页查询薪资管道列表
     * @param queryReqDTO 查询参数DTO
     * @return 包含 VO 的分页结果
     */
    PageResult<CalcPipelineInfoVO> pagePipelineInfo(CalcPipelineInfoQueryReqDTO queryReqDTO);

    /**
     * 获取当前系统启用的默认薪资计算管道
     * @return 默认管道视图对象
     */
    CalcPipelineInfoVO getDefaultPipeline();

}
