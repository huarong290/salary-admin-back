package com.salary.admin.service.impl.salary;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.convert.salary.calcpipelinestep.CalcPipelineStepConvert;
import com.salary.admin.mapper.ext.SalaryCalcPipelineStepExtMapper;
import com.salary.admin.model.dto.calcpipelinestep.CalcPipelineStepAddReqDTO;
import com.salary.admin.model.entity.salary.SalaryCalcPipelineStep;
import com.salary.admin.model.vo.calcpipelinestep.CalcPipelineStepVO;
import com.salary.admin.service.salary.ISalaryCalcPipelineStepService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 薪资计算管道步骤表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SalaryCalcPipelineStepServiceImpl extends ServiceImpl<SalaryCalcPipelineStepExtMapper, SalaryCalcPipelineStep> implements ISalaryCalcPipelineStepService {

    private SalaryCalcPipelineStepExtMapper salaryCalcPipelineStepExtMapper;

    private final CalcPipelineStepConvert pipelineStepConvert;


    // ======================== 1.新增操作 (Add) ========================

    // ======================== 2. 删除操作 (Delete) ========================

    // ======================== 3. 修改操作 (Update) ========================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchSaveSteps(String pipelineCode, Integer pipelineVersion, List<CalcPipelineStepAddReqDTO> stepAddDTOList) {
        // 1. 删除该管道+版本下的所有旧数据 (物理或逻辑删除取决于你的配置)
        this.remove(Wrappers.<SalaryCalcPipelineStep>lambdaQuery()
                .eq(SalaryCalcPipelineStep::getPipelineCode, pipelineCode)
                .eq(SalaryCalcPipelineStep::getPipelineVersion, pipelineVersion));

        // 2. 将传入的 DTO 转换为 Entity，并强制绑定 Code 和 Version 以防伪造数据
        List<SalaryCalcPipelineStep> newEntities = stepAddDTOList.stream().map(dto -> {
            SalaryCalcPipelineStep entity = pipelineStepConvert.toEntity(dto);
            entity.setPipelineCode(pipelineCode);
            entity.setPipelineVersion(pipelineVersion);
            return entity;
        }).toList();

        // 3. 批量插入新数据
        if (!newEntities.isEmpty()) {
            return this.saveBatch(newEntities);
        }
        return true;
    }
    // ======================== 4. 查询操作 (Query) ========================

    @Override
    public List<CalcPipelineStepVO> listSteps(String pipelineCode, Integer pipelineVersion) {
        List<SalaryCalcPipelineStep> stepList = this.list(Wrappers.<SalaryCalcPipelineStep>lambdaQuery()
                .eq(SalaryCalcPipelineStep::getPipelineCode, pipelineCode)
                .eq(SalaryCalcPipelineStep::getPipelineVersion, pipelineVersion)
                // 核心逻辑：引擎执行高度依赖排序，必须严格按照 阶段 -> 顺序 的优先级升序排列
                .orderByAsc(SalaryCalcPipelineStep::getStage, SalaryCalcPipelineStep::getSortOrder));

        return pipelineStepConvert.toVOList(stepList);
    }
}
