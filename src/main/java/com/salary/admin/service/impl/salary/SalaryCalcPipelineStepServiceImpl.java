package com.salary.admin.service.impl.salary;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.convert.salary.calcpipelinestep.CalcPipelineStepConvert;
import com.salary.admin.mapper.ext.SalaryCalcPipelineStepExtMapper;
import com.salary.admin.model.dto.calcpipelinestep.CalcPipelineStepBatchItemDTO;
import com.salary.admin.model.entity.salary.SalaryCalcPipelineStep;
import com.salary.admin.model.vo.calcpipelinestep.CalcPipelineStepVO;
import com.salary.admin.service.salary.ISalaryCalcPipelineStepService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

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

    private final CalcPipelineStepConvert calcPipelineStepConvert;


    // ======================== 1.新增操作 (Add) ========================

    // ======================== 2. 删除操作 (Delete) ========================

    // ======================== 3. 修改操作 (Update) ========================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchSaveSteps(String pipelineCode, Integer pipelineVersion, List<CalcPipelineStepBatchItemDTO> stepList) {
        // 1. 删除该管道+版本下的所有旧数据 (物理或逻辑删除取决于你的配置)
        this.remove(Wrappers.<SalaryCalcPipelineStep>lambdaQuery()
                .eq(SalaryCalcPipelineStep::getPipelineCode, pipelineCode)
                .eq(SalaryCalcPipelineStep::getPipelineVersion, pipelineVersion));
        // 2. 如果前端传空数组，代表清空，直接返回成功
        if (CollectionUtils.isEmpty(stepList)) {
            return true;
        }

        // 3. 组装新实体：强制注入上下文变量，无视前端可能伪造的数据
        List<SalaryCalcPipelineStep> entityList = stepList.stream().map(dto -> {
            SalaryCalcPipelineStep entity = calcPipelineStepConvert.toEntity(dto); // MapStruct 转换

            // 强制绑定 URL 中的 Code 和 Version
            entity.setPipelineCode(pipelineCode);
            entity.setPipelineVersion(pipelineVersion);

            return entity;
        }).collect(Collectors.toList());

        // 3. 批量插入新数据
        if (!entityList.isEmpty()) {
            return this.saveBatch(entityList);
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

        return calcPipelineStepConvert.toVOList(stepList);
    }
}
