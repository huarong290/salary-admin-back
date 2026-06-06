package com.salary.admin.service.impl.salary.engine;

import com.salary.admin.exception.BusinessException;
import com.salary.admin.model.dto.engine.SalaryCalcSingleReqDTO;
import com.salary.admin.model.dto.salary.StepExecResult;
import com.salary.admin.model.dto.salary.snapshot.ArchiveSnapshot;
import com.salary.admin.model.entity.salary.SalaryCalcPipelineStep;
import com.salary.admin.model.entity.salary.SalaryItemConfig;
import com.salary.admin.service.salary.ISalaryCalcContextService;
import com.salary.admin.service.salary.ISalaryCalcPipelineStepService;
import com.salary.admin.service.salary.ISalaryItemConfigService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 薪资核算模板基类 (泛型 T 代表最终产出的结果类型)
 */
@Slf4j
public abstract class AbstractSalaryProcessor<T> {

    @Resource
    protected ISalaryCalcPipelineStepService stepService;
    @Resource
    protected ISalaryCalcContextService contextService;
    @Resource
    protected ISalaryItemConfigService configService;
    @Resource
    protected PipelineStepExecutor stepExecutor;

    /**
     * 引擎驱动主干流程 (注意：此处不加事务，交由持久化子类控制)
     */
    public T process(SalaryCalcSingleReqDTO reqDTO) {
        // 1. 解析管道版本
        String pipelineCode = StringUtils.isNotBlank(reqDTO.getPipelineCode()) ? reqDTO.getPipelineCode() : "OFFICIAL_STAFF_2024";
        Integer pipelineVersion = reqDTO.getPipelineVersion() != null ? reqDTO.getPipelineVersion() : 1;

        // 2. 获取编排图纸
        List<SalaryCalcPipelineStep> steps = getPipelineSteps(pipelineCode, pipelineVersion);

        // 3. 构建核算上下文
        Map<String, Object> env = contextService.buildEmployeeContext(
                reqDTO.getPeriodId(), reqDTO.getEmployeeId(), pipelineCode, pipelineVersion, reqDTO.getArchiveId());

        // 4. 批量加载字典配置 (后续可优化为二级缓存架构)
        Map<String, SalaryItemConfig> configMap = loadConfigs(steps);

        // 5. 初始化领域聚合器，并注入档案快照
        SalaryCalcAggregator aggregator = new SalaryCalcAggregator();
        aggregator.getSnapshot().setUsedArchives((List<ArchiveSnapshot>) env.get("_usedArchives"));

        // 6. 驱动流水线
        for (SalaryCalcPipelineStep step : steps) {
            // 调用独立组件执行脚本 获取标准化包装结果
            StepExecResult result = stepExecutor.executeStep(step, env, reqDTO.getPeriodId(), reqDTO.getEmployeeId());
            // 拦截执行器发出的“跳过”信号 如果是跳过状态，只放个 0 进环境变量防报错，不进聚合器！
            if (result.isSkip()) {
                // 虽然跳过不展示，但必须往上下文里压入一个 0，防止下游公式引用该字段时抛出空指针
                env.put(step.getRuleCode(), BigDecimal.ZERO);
                continue; // 直接进入下一轮，不聚合，不生成快照和落库明细！
            }
            // 压入上下文，供后续依赖节点使用并送入聚合器累加、生成快照
            env.put(step.getRuleCode(), result.getAmount());

            // 聚合器进行纯净的内存累加
            aggregator.accumulate(step, result.getAmount(), configMap.get(step.getRuleCode()));
        }

        // 7. 关账，补全汇总信息
        aggregator.finalizeSnapshot(env, pipelineCode);

        // 8. 抛给子类进行差异化处理 (持久化落地 or 直接封装返回)
        return handleResult(reqDTO, aggregator, env);
    }

    private List<SalaryCalcPipelineStep> getPipelineSteps(String code, Integer version) {
        List<SalaryCalcPipelineStep> steps = stepService.lambdaQuery()
                .eq(SalaryCalcPipelineStep::getPipelineCode, code)
                .eq(SalaryCalcPipelineStep::getPipelineVersion, version)
                .eq(SalaryCalcPipelineStep::getStatus, 1)
                .orderByAsc(SalaryCalcPipelineStep::getStage)
                .orderByAsc(SalaryCalcPipelineStep::getSortOrder)
                .list();
        if (steps.isEmpty()) throw new BusinessException("薪资管道未配置有效的核算步骤！");
        return steps;
    }

    private Map<String, SalaryItemConfig> loadConfigs(List<SalaryCalcPipelineStep> steps) {
        Set<String> ruleCodes = steps.stream().map(SalaryCalcPipelineStep::getRuleCode).collect(Collectors.toSet());
        return configService.lambdaQuery()
                .in(SalaryItemConfig::getItemCode, ruleCodes)
                .list()
                .stream()
                .collect(Collectors.toMap(SalaryItemConfig::getItemCode, c -> c, (v1, v2) -> v1));
    }

    /**
     * 留给具体实现类的钩子方法
     */
    protected abstract T handleResult(SalaryCalcSingleReqDTO reqDTO, SalaryCalcAggregator aggregator, Map<String, Object> env);
}
