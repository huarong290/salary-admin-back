package com.salary.admin.service.impl.salary;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.common.PageResult;
import com.salary.admin.convert.salary.calcpipelineinfo.CalcPipelineInfoConvert;
import com.salary.admin.mapper.ext.SalaryCalcPipelineInfoExtMapper;
import com.salary.admin.model.dto.salary.calcpipelineinfo.CalcPipelineInfoAddReqDTO;
import com.salary.admin.model.dto.salary.calcpipelineinfo.CalcPipelineInfoEditReqDTO;
import com.salary.admin.model.dto.salary.calcpipelineinfo.CalcPipelineInfoQueryReqDTO;
import com.salary.admin.model.entity.salary.SalaryCalcPipelineInfo;
import com.salary.admin.model.entity.salary.SalaryCalcPipelineStep;
import com.salary.admin.model.vo.calcpipelineinfo.CalcPipelineInfoVO;
import com.salary.admin.service.salary.ISalaryCalcPipelineInfoService;
import com.salary.admin.service.salary.ISalaryCalcPipelineStepService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class SalaryCalcPipelineInfoServiceImpl extends ServiceImpl<SalaryCalcPipelineInfoExtMapper, SalaryCalcPipelineInfo> implements ISalaryCalcPipelineInfoService {
    // 注入 MapStruct 转换器
    private final CalcPipelineInfoConvert pipelineInfoConvert;
    // 注入 Step 服务，用于升版本时的级联复制
    private final ISalaryCalcPipelineStepService iSalaryCalcPipelineStepService;

    // ======================== 1.新增操作 (Add) ========================
    @Override
    public boolean addPipeline(CalcPipelineInfoAddReqDTO addDTO) {
        SalaryCalcPipelineInfo entity = pipelineInfoConvert.toEntity(addDTO);
        return this.save(entity);
    }
    // ======================== 2. 删除操作 (Delete) ========================

    // ======================== 3. 修改操作 (Update) ========================
    @Override
    public boolean updatePipeline(CalcPipelineInfoEditReqDTO editDTO) {
        SalaryCalcPipelineInfo entity = pipelineInfoConvert.toEntity(editDTO);
        return this.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setDefaultPipeline(Long id) {
        // 1. 将所有记录的 default_flag 重置为 0
        LambdaUpdateWrapper<SalaryCalcPipelineInfo> resetWrapper = Wrappers.lambdaUpdate();
        resetWrapper.set(SalaryCalcPipelineInfo::getDefaultFlag, 0);
        this.update(resetWrapper);

        // 2. 将指定 ID 的记录设为 1
        LambdaUpdateWrapper<SalaryCalcPipelineInfo> setWrapper = Wrappers.lambdaUpdate();
        setWrapper.set(SalaryCalcPipelineInfo::getDefaultFlag, 1)
                .eq(SalaryCalcPipelineInfo::getId, id);
        return this.update(setWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long copyAndUpgradePipeline(Long sourceId) {
        // 1. 获取源管道
        SalaryCalcPipelineInfo sourceInfo = this.getById(sourceId);
        if (sourceInfo == null) {
            throw new RuntimeException("源管道不存在");
        }

        // 2. 构造新版本主表信息
        SalaryCalcPipelineInfo newInfo = new SalaryCalcPipelineInfo();
        newInfo.setPipelineCode(sourceInfo.getPipelineCode());
        newInfo.setPipelineName(sourceInfo.getPipelineName() + " (V" + (sourceInfo.getVersion() + 1) + ")");
        newInfo.setVersion(sourceInfo.getVersion() + 1);
        newInfo.setDefaultFlag(0); // 新版本默认不启用为系统默认
        newInfo.setStatus(1);
        newInfo.setRemark("从版本 V" + sourceInfo.getVersion() + " 复制升级");
        this.save(newInfo);

        // 3. 级联复制步骤明细表 (Step)
        List<SalaryCalcPipelineStep> sourceSteps = iSalaryCalcPipelineStepService.list(Wrappers.<SalaryCalcPipelineStep>lambdaQuery()
                .eq(SalaryCalcPipelineStep::getPipelineCode, sourceInfo.getPipelineCode())
                .eq(SalaryCalcPipelineStep::getPipelineVersion, sourceInfo.getVersion()));

        if (!sourceSteps.isEmpty()) {
            List<SalaryCalcPipelineStep> newSteps = sourceSteps.stream().map(step -> {
                SalaryCalcPipelineStep newStep = new SalaryCalcPipelineStep();
                // 排除主键，由数据库自动生成
                newStep.setPipelineCode(newInfo.getPipelineCode());
                newStep.setPipelineVersion(newInfo.getVersion()); // 绑定新版本号
                newStep.setRuleCode(step.getRuleCode());
                newStep.setRuleName(step.getRuleName());
                newStep.setRuleType(step.getRuleType());
                newStep.setConditionScript(step.getConditionScript());
                newStep.setStage(step.getStage());
                newStep.setSortOrder(step.getSortOrder());
                newStep.setBlockFlag(step.getBlockFlag());
                newStep.setSkipIfNull(step.getSkipIfNull());
                newStep.setStatus(step.getStatus());
                return newStep;
            }).collect(Collectors.toList());

            iSalaryCalcPipelineStepService.saveBatch(newSteps);
        }

        return newInfo.getId();
    }
    // ======================== 4. 查询操作 (Query) ========================
    @Override
    public PageResult<CalcPipelineInfoVO> pagePipelineInfo(CalcPipelineInfoQueryReqDTO queryReqDTO) {
        Page<SalaryCalcPipelineInfo> page = new Page<>(queryReqDTO.getPageNum(), queryReqDTO.getPageSize());
        LambdaQueryWrapper<SalaryCalcPipelineInfo> wrapper = Wrappers.lambdaQuery();

        // 组装关键字模糊查询逻辑
        if (StrUtil.isNotBlank(queryReqDTO.getKeyword())) {
            wrapper.and(w -> w.like(SalaryCalcPipelineInfo::getPipelineCode, queryReqDTO.getKeyword())
                    .or()
                    .like(SalaryCalcPipelineInfo::getPipelineName, queryReqDTO.getKeyword()));
        }
        // 组装状态筛选
        if (queryReqDTO.getStatus() != null) {
            wrapper.eq(SalaryCalcPipelineInfo::getStatus, queryReqDTO.getStatus());
        }

        // 默认按创建时间倒序
        wrapper.orderByDesc(SalaryCalcPipelineInfo::getCreateTime);

        // 1. 获取 MyBatis-Plus 的原生分页结果 (包含 Entity 数据)
        IPage<SalaryCalcPipelineInfo> entityPage = this.page(page, wrapper);

        // 2. 利用 MapStruct 转换器，将 Entity 的 Records 列表批量转换为 VO 列表
        List<CalcPipelineInfoVO> voList = pipelineInfoConvert.toVOList(entityPage.getRecords());

        // 3. 使用你封装的 PageResult 场景2：传入原始分页对象和转换后的数据列表
        return PageResult.of(entityPage, voList);
    }

    @Override
    public CalcPipelineInfoVO getDefaultPipeline() {
        SalaryCalcPipelineInfo entity = this.getOne(Wrappers.<SalaryCalcPipelineInfo>lambdaQuery()
                .eq(SalaryCalcPipelineInfo::getDefaultFlag, 1)
                .eq(SalaryCalcPipelineInfo::getStatus, 1)
                .last("LIMIT 1"));
        return pipelineInfoConvert.toVO(entity);
    }

    @Override
    public SalaryCalcPipelineInfo getDefaultPipelineEntity() {
        return this.getOne(Wrappers.<SalaryCalcPipelineInfo>lambdaQuery()
                .eq(SalaryCalcPipelineInfo::getDefaultFlag, 1)
                .eq(SalaryCalcPipelineInfo::getStatus, 1)
                .last("LIMIT 1"));
    }

    @Override
    public List<SalaryCalcPipelineInfo> listEnabledPipelines() {
        return this.list(Wrappers.<SalaryCalcPipelineInfo>lambdaQuery()
                .eq(SalaryCalcPipelineInfo::getStatus, 1)
                .orderByAsc(SalaryCalcPipelineInfo::getPipelineCode)
                .orderByDesc(SalaryCalcPipelineInfo::getVersion));
    }

    @Override
    public Integer resolveEnabledVersion(String pipelineCode) {
        if (StrUtil.isBlank(pipelineCode)) {
            return null;
        }

        // 1. 取出该编码下所有启用的版本: 默认版本优先, 其次取版本号最大的
        List<SalaryCalcPipelineInfo> candidates = this.list(Wrappers.<SalaryCalcPipelineInfo>lambdaQuery()
                .eq(SalaryCalcPipelineInfo::getPipelineCode, pipelineCode)
                .eq(SalaryCalcPipelineInfo::getStatus, 1)
                .orderByDesc(SalaryCalcPipelineInfo::getDefaultFlag)
                .orderByDesc(SalaryCalcPipelineInfo::getVersion));
        if (candidates.isEmpty()) {
            return null;
        }

        // 2. 优先返回"真正有启用步骤"的版本, 避免选到一个空壳管道
        for (SalaryCalcPipelineInfo candidate : candidates) {
            if (hasEnabledSteps(pipelineCode, candidate.getVersion())) {
                return candidate.getVersion();
            }
        }

        // 3. 都没有步骤时返回首个候选, 交由调用方在异常信息里列出可用管道
        return candidates.get(0).getVersion();
    }

    @Override
    public boolean hasEnabledSteps(String pipelineCode, Integer pipelineVersion) {
        if (StrUtil.isBlank(pipelineCode) || pipelineVersion == null) {
            return false;
        }
        return iSalaryCalcPipelineStepService.lambdaQuery()
                .eq(SalaryCalcPipelineStep::getPipelineCode, pipelineCode)
                .eq(SalaryCalcPipelineStep::getPipelineVersion, pipelineVersion)
                .eq(SalaryCalcPipelineStep::getStatus, 1)
                .count() > 0;
    }

    @Override
    public String describeAvailablePipelines() {
        List<SalaryCalcPipelineInfo> enabledPipelines = listEnabledPipelines();
        if (enabledPipelines.isEmpty()) {
            return "当前系统没有任何启用状态的薪资管道，请先在【引擎配置 - 计算管道】中新增。";
        }

        // 拼装成 "编码(V版本,默认)" 的形式, 让排查的人一眼看出该选哪个
        String detail = enabledPipelines.stream()
                .map(info -> String.format("%s(V%s%s)", info.getPipelineCode(), info.getVersion(),
                        Integer.valueOf(1).equals(info.getDefaultFlag()) ? ",默认" : ""))
                .collect(Collectors.joining(", "));
        return "当前可用管道: " + detail;
    }
}
