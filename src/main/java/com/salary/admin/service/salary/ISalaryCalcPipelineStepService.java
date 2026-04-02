package com.salary.admin.service.salary;

import com.baomidou.mybatisplus.extension.service.IService;
import com.salary.admin.model.dto.calcpipelinestep.CalcPipelineStepBatchItemDTO;
import com.salary.admin.model.entity.salary.SalaryCalcPipelineStep;
import com.salary.admin.model.vo.calcpipelinestep.CalcPipelineStepVO;

import java.util.List;

/**
 * <p>
 * 薪资计算流程管道步骤表 服务类
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
public interface ISalaryCalcPipelineStepService extends IService<SalaryCalcPipelineStep> {

    // ======================== 1.新增操作 (Add) ========================

    // ======================== 2. 删除操作 (Delete) ========================

    // ======================== 3. 修改操作 (Update) ========================
    /**
     * 批量保存管道步骤（全量覆盖）
     * 业务场景：前端通过拖拽排好序后，将整个列表传回后端，后端先删后插保证数据一致。
     * @param pipelineCode 管道编码
     * @param pipelineVersion 版本
     * @param stepAddDTOList 前端传来的步骤集合
     * @return 是否成功
     */
    boolean batchSaveSteps(String pipelineCode, Integer pipelineVersion, List<CalcPipelineStepBatchItemDTO> stepAddDTOList);
    // ======================== 4. 查询操作 (Query) ========================
    /**
     * 根据管道编码和版本，获取该管道下的执行步骤
     * @param pipelineCode 管道编码
     * @param pipelineVersion 管道版本
     * @return 按 stage 和 sortOrder 排序后的 VO 列表
     */
    List<CalcPipelineStepVO> listSteps(String pipelineCode, Integer pipelineVersion);
}
