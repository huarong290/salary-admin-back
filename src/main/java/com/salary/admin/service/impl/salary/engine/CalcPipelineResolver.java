package com.salary.admin.service.impl.salary.engine;

import com.salary.admin.exception.BusinessException;
import com.salary.admin.model.dto.engine.SalaryCalcSingleReqDTO;
import com.salary.admin.model.dto.salary.ResolvedPipeline;
import com.salary.admin.model.entity.salary.SalaryCalcPipelineInfo;
import com.salary.admin.service.salary.ISalaryCalcPipelineInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 薪资计算管道解析器
 * <p>
 * 解决"管道编码/版本写死在代码里"的问题：算薪时不再假设库里一定有某个固定管道，
 * 而是按下面的优先级解析出本次真正要用的管道：
 * <pre>
 * L1 调用方显式指定 pipelineCode (可选 pipelineVersion)
 *    → 指定了就必须可用，否则直接抛错，绝不静默兜底(宁可不算，也不能算错工资)
 * L2 全局默认管道: salary_calc_pipeline_info.default_flag = 1 且 status = 1
 * L3 库中唯一可用的管道 (仅当"恰好只有一个"可用时才兜底)
 * L4 以上都没有 → 快速失败，并在异常信息中列出当前可用管道
 * </pre>
 * 版本解析规则：显式传了版本则精确匹配；只给编码时优先取 default_flag=1 的启用版本，
 * 其次取版本号最大的可用版本（且必须存在启用状态的核算步骤）。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CalcPipelineResolver {

    private final ISalaryCalcPipelineInfoService pipelineInfoService;


    /**
     * 解析本次核算使用的薪资管道
     *
     * @param reqDTO 单人核算请求(可能携带 pipelineCode / pipelineVersion)
     * @return 解析后的管道坐标
     */
    public ResolvedPipeline resolve(SalaryCalcSingleReqDTO reqDTO) {
        // ======================== L1 调用方显式指定 ========================
        if (StringUtils.isNotBlank(reqDTO.getPipelineCode())) {
            // 只给编码没给版本时，由库里的配置解析出可用版本
            Integer version = reqDTO.getPipelineVersion() != null
                    ? reqDTO.getPipelineVersion()
                    : pipelineInfoService.resolveEnabledVersion(reqDTO.getPipelineCode());
            if (version == null || !pipelineInfoService.hasEnabledSteps(reqDTO.getPipelineCode(), version)) {
                throw new BusinessException(String.format(
                        "指定的薪资管道不可用：code=%s, version=%s。%s",
                        reqDTO.getPipelineCode(), version, pipelineInfoService.describeAvailablePipelines()));
            }
            log.debug("📐 使用调用方指定的薪资管道: {} - V{}", reqDTO.getPipelineCode(), version);
            return build(reqDTO.getPipelineCode(), version, false);
        }

        // ======================== L2 全局默认管道 ========================
        SalaryCalcPipelineInfo defaultPipeline = pipelineInfoService.getDefaultPipelineEntity();
        if (defaultPipeline != null
                && pipelineInfoService.hasEnabledSteps(defaultPipeline.getPipelineCode(), defaultPipeline.getVersion())) {
            log.debug("📐 使用默认薪资管道: {} - V{}", defaultPipeline.getPipelineCode(), defaultPipeline.getVersion());
            return build(defaultPipeline.getPipelineCode(), defaultPipeline.getVersion(), true);
        }
        if (defaultPipeline != null) {
            log.warn("⚠️ 默认薪资管道 [{} - V{}] 没有启用状态的核算步骤，继续尝试兜底解析",
                    defaultPipeline.getPipelineCode(), defaultPipeline.getVersion());
        }

        // ======================== L3 唯一可用管道兜底 ========================
        // 只有"恰好一个"可用时才敢兜底：多个可用说明配置存在歧义，交给人工指定更安全
        List<SalaryCalcPipelineInfo> usablePipelines = pipelineInfoService.listEnabledPipelines().stream()
                .filter(info -> pipelineInfoService.hasEnabledSteps(info.getPipelineCode(), info.getVersion()))
                .collect(Collectors.toList());
        if (usablePipelines.size() == 1) {
            SalaryCalcPipelineInfo onlyPipeline = usablePipelines.get(0);
            log.warn("⚠️ 未配置默认薪资管道，已自动兜底到系统唯一可用管道 [{} - V{}]，建议在【引擎配置】中显式设置默认管道",
                    onlyPipeline.getPipelineCode(), onlyPipeline.getVersion());
            return build(onlyPipeline.getPipelineCode(), onlyPipeline.getVersion(), true);
        }

        // ======================== L4 快速失败 ========================
        throw new BusinessException("未找到可用的薪资计算管道，请先在【引擎配置 - 计算管道】中设置默认管道。"
                + pipelineInfoService.describeAvailablePipelines());
    }

    /**
     * 组装解析结果
     *
     * @param pipelineCode    管道编码
     * @param pipelineVersion 管道版本
     * @param fallback        是否为系统兜底解析
     * @return 管道解析结果
     */
    private ResolvedPipeline build(String pipelineCode, Integer pipelineVersion, boolean fallback) {
        return ResolvedPipeline.builder()
                .pipelineCode(pipelineCode)
                .pipelineVersion(pipelineVersion)
                .fallback(fallback)
                .build();
    }
}
