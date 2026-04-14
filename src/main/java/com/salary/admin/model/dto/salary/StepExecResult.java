package com.salary.admin.model.dto.salary;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
/**
 * 流水线节点执行结果包装器 (Result Object Pattern)
 * <p>
 * 架构意义：
 * 替代原始的 BigDecimal 或 null 返回值，赋予业务控制流以明确的语义。
 * 杜绝 NullPointerException，提升代码自文档化能力。
 */
@Getter
@Builder
public class StepExecResult {
    /**
     * 是否跳过当前节点 (如：条件不满足、容错降级、金额为0跳过)
     */
    private boolean skip;
    /**
     * 计算产生的金额 (如果被跳过，该值严格保证为 BigDecimal.ZERO)
     */
    private BigDecimal amount;

    // =========================================================================
    // 🌟 静态工厂方法：提供极具语义化的对象构建方式
    // =========================================================================

    /**
     * 明确声明：跳过该节点
     */
    public static StepExecResult skip() {
        return StepExecResult.builder()
                .skip(true)
                .amount(BigDecimal.ZERO) // 保底为0，防止下游误用空对象
                .build();
    }

    /**
     * 明确声明：计算成功，并返回金额
     */
    public static StepExecResult success(BigDecimal amount) {
        return StepExecResult.builder()
                .skip(false)
                .amount(amount != null ? amount : BigDecimal.ZERO) // 防御性编程
                .build();
    }
}
