package com.salary.admin.model.dto.salary;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 薪资计算管道解析结果
 * <p>
 * 引擎最终采用的"管道坐标"(编码 + 版本)。调用方不指定时，由 {@code CalcPipelineResolver}
 * 按"默认管道 > 唯一可用管道"的顺序解析得出，并记录是否走了兜底，便于审计与问题定位。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ResolvedPipeline", description = "薪资计算管道解析结果")
public class ResolvedPipeline implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 管道编码
     */
    @Schema(description = "管道编码")
    private String pipelineCode;

    /**
     * 管道版本
     */
    @Schema(description = "管道版本")
    private Integer pipelineVersion;

    /**
     * 是否由系统兜底解析得出 (true = 调用方未指定, false = 调用方显式指定)
     */
    @Schema(description = "是否由系统兜底解析得出")
    private boolean fallback;
}
