package com.salary.admin.service.salary;

import com.baomidou.mybatisplus.extension.service.IService;
import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.salary.calcpipelineinfo.CalcPipelineInfoAddReqDTO;
import com.salary.admin.model.dto.salary.calcpipelineinfo.CalcPipelineInfoEditReqDTO;
import com.salary.admin.model.dto.salary.calcpipelineinfo.CalcPipelineInfoQueryReqDTO;
import com.salary.admin.model.entity.salary.SalaryCalcPipelineInfo;
import com.salary.admin.model.vo.calcpipelineinfo.CalcPipelineInfoVO;

import java.util.List;

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

    /**
     * 获取默认管道的实体对象 (供计算引擎内部解析使用, 区别于给前端展示的 getDefaultPipeline)
     * @return 默认管道实体; 未配置 default_flag=1 的启用管道时返回 null
     */
    SalaryCalcPipelineInfo getDefaultPipelineEntity();

    /**
     * 查询所有启用状态 (status = 1) 的管道
     * @return 启用管道列表 (按编码升序、版本降序)
     */
    List<SalaryCalcPipelineInfo> listEnabledPipelines();

    /**
     * 解析某个管道编码当前应使用的版本号
     * 业务逻辑：优先取该编码下 default_flag=1 的启用版本, 其次取版本号最大的启用版本,
     *          且该版本必须存在启用状态的核算步骤
     * @param pipelineCode 管道编码
     * @return 可用版本号; 该编码下没有任何可用版本时返回 null
     */
    Integer resolveEnabledVersion(String pipelineCode);

    /**
     * 判断某个管道版本下是否存在启用状态的核算步骤
     * @param pipelineCode 管道编码
     * @param pipelineVersion 管道版本
     * @return true = 存在可用步骤
     */
    boolean hasEnabledSteps(String pipelineCode, Integer pipelineVersion);

    /**
     * 生成当前"可用管道"的简短描述, 用于把异常信息变得可操作
     * 形如：当前可用管道: OFFICIAL_STAFF_2026(V1,默认), HOURLY_STAFF_2026(V1)
     * @return 可用管道描述
     */
    String describeAvailablePipelines();

}
