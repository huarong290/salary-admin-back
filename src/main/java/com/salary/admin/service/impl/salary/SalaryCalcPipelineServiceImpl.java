package com.salary.admin.service.impl.salary;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.common.PageResult;
import com.salary.admin.convert.salary.calcpipeline.CalcPipelineConvert;
import com.salary.admin.exception.BusinessException;
import com.salary.admin.mapper.ext.SalaryCalcPipelineExtMapper;
import com.salary.admin.model.dto.calcpipeline.CalcPipelineAddReqDTO;
import com.salary.admin.model.dto.calcpipeline.CalcPipelineEditReqDTO;
import com.salary.admin.model.dto.calcpipeline.CalcPipelineItemDTO;
import com.salary.admin.model.dto.calcpipeline.CalcPipelineQueryReqDTO;
import com.salary.admin.model.entity.salary.SalaryCalcPipeline;
import com.salary.admin.model.vo.calcpipeline.CalcPipelineVO;
import com.salary.admin.service.salary.ISalaryCalcPipelineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * <p>
 * 薪资计算流程管道表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SalaryCalcPipelineServiceImpl extends ServiceImpl<SalaryCalcPipelineExtMapper, SalaryCalcPipeline> implements ISalaryCalcPipelineService {

    private final SalaryCalcPipelineExtMapper salaryCalcPipelineExtMapper;

    private final CalcPipelineConvert calcPipelineConvert;

    // ======================== 1. 新增操作 (Create) ========================
    @Override
    public Long addPipeline(CalcPipelineAddReqDTO reqDTO) {
        SalaryCalcPipeline pipeline = calcPipelineConvert.toEntity(reqDTO);
        // 默认设置为启用状态
        if (pipeline.getStatus() == null) {
            pipeline.setStatus(1);
        }
        this.save(pipeline);
        return pipeline.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean savePipelineBatchMode(String pipelineCode, List<CalcPipelineItemDTO> pipelineDTOs) {
        if (org.apache.commons.lang3.StringUtils.isBlank(pipelineCode)) {
            throw new BusinessException("流程编码不能为空");
        }

        // 1. 物理清理：全量删除该编码下现有的所有流程步骤
        salaryCalcPipelineExtMapper.delete(new LambdaQueryWrapper<SalaryCalcPipeline>()
                .eq(SalaryCalcPipeline::getPipelineCode, pipelineCode));

        // 2. 如果前端传空数组，代表清空整个管道，直接返回
        if (CollectionUtils.isEmpty(pipelineDTOs)) {
            return true;
        }

        // 3.  将 DTO 列表转换为数据库 Entity 列表
        List<SalaryCalcPipeline> pipelines = calcPipelineConvert.toEntityList(pipelineDTOs);

        // 4. 强制赋予 pipelineCode 并严格按数组索引重新分配 sortOrder
        for (int i = 0; i < pipelines.size(); i++) {
            SalaryCalcPipeline step = pipelines.get(i);

            step.setId(null); // 清空可能被恶意带入的ID，强制新增
            step.setPipelineCode(pipelineCode); // 强制归属当前管道
            step.setSortOrder(i + 1); // 💡 核心：无视前端传的排序，绝对信任后端数组索引 (1, 2, 3...)

            if (step.getStatus() == null) {
                step.setStatus(1); // 默认启用
            }
        }

        // 5. 批量插入新洗牌后的流程
        return this.saveBatch(pipelines);
    }

    // ======================== 2. 删除操作 (Delete) ========================
    @Override
    public boolean deletePipelineById(Long id, boolean logicalDelete) {
        if (logicalDelete) {
            // 触发 @TableLogic 逻辑删除
            return this.removeById(id);
        } else {
            // 物理删除，用于运维清理历史冗余数据
            return salaryCalcPipelineExtMapper.physicalDeleteById(id) > 0;
        }
    }

    @Override
    public boolean deletePipelineByIds(List<Long> ids, boolean logicalDelete) {
        if (CollectionUtils.isEmpty(ids)){
            return false;
        }
        if (logicalDelete) {
            return this.removeByIds(ids);
        } else {
            return salaryCalcPipelineExtMapper.physicalDeleteByIds(ids) > 0;
        }
    }
    // ======================== 3. 修改操作 (Update) ========================
    @Override
    public boolean editPipeline(CalcPipelineEditReqDTO reqDTO) {
        if (reqDTO.getId() == null) {
            throw new BusinessException("更新操作必须指定管道ID");
        }
        SalaryCalcPipeline pipeline = calcPipelineConvert.toEntity(reqDTO);
        return this.updateById(pipeline);
    }
    // ======================== 4. 查询操作 (Read) ========================
    @Override
    public PageResult<CalcPipelineVO> getPipelinePage(CalcPipelineQueryReqDTO reqDTO) {
        Page<SalaryCalcPipeline> page = new Page<>(reqDTO.getPageNum(), reqDTO.getPageSize());
        LambdaQueryWrapper<SalaryCalcPipeline> wrapper = new LambdaQueryWrapper<>();

        // 业务筛选：按管道编码或状态
        wrapper.eq(reqDTO.getPipelineCode() != null, SalaryCalcPipeline::getPipelineCode, reqDTO.getPipelineCode())
                .eq(reqDTO.getStatus() != null, SalaryCalcPipeline::getStatus, reqDTO.getStatus());

        // 核心优化：多级排序。先按 Stage (计算阶段) 排序，再按 SortOrder (阶段内顺序) 排序
        wrapper.orderByAsc(SalaryCalcPipeline::getStage, SalaryCalcPipeline::getSortOrder);

        IPage<SalaryCalcPipeline> resultPage = this.page(page, wrapper);
        return PageResult.of(resultPage, calcPipelineConvert.toVOList(resultPage.getRecords()));
    }

    @Override
    public CalcPipelineVO getPipelineDetail(Long id) {
        SalaryCalcPipeline pipeline = this.getById(id);
        if (pipeline == null) {
            throw new BusinessException("该流程管道配置不存在");
        }
        return calcPipelineConvert.toVO(pipeline);
    }
}
