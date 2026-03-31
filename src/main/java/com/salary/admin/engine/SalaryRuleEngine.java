package com.salary.admin.engine;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.googlecode.aviator.Options;
import com.salary.admin.exception.BusinessException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/**
 * 薪资规则动态计算引擎 (基于 AviatorScript)
 */
@Slf4j
@Component
public class SalaryRuleEngine {

    /**
     * 初始化引擎全局配置
     */
    @PostConstruct
    public void init() {
        //  1. 开启金融级高精度计算：所有浮点数自动转为 BigDecimal
        AviatorEvaluator.setOption(Options.ALWAYS_PARSE_FLOATING_POINT_NUMBER_INTO_DECIMAL, true);

        // 🌟 2. 开启整数除法自动转浮点数 (防止 21.5 / 21.75 变成 0)
        AviatorEvaluator.setOption(Options.ALWAYS_PARSE_INTEGRAL_NUMBER_INTO_DECIMAL, true);

        // 🌟 3. 设置计算精度 MathContext (使用 DECIMAL128，极其精确)
        AviatorEvaluator.setOption(Options.MATH_CONTEXT, java.math.MathContext.DECIMAL128);

        log.info("💰 薪资计算引擎 (AviatorScript) 初始化完成，已开启全量 BigDecimal 精度保护。");

        // TODO: 后续我们还可以这里注册自定义函数，比如：AviatorEvaluator.addFunction(new TaxFunction());
    }

    /**
     * 核心计算方法
     *
     * @param script     公式脚本 (例如: "baseSalary * (attendanceDays / monthDays)")
     * @param envContext 环境变量上下文 (包含了员工底薪、考勤天数等)
     * @return 计算结果 (安全返回 BigDecimal)
     */
    public BigDecimal execute(String script, Map<String, Object> envContext) {
        try {
            // 1. 编译脚本 (Aviator 内部有 LRU 缓存，相同的脚本不会重复编译，性能极高)
            Expression compiledExp = AviatorEvaluator.compile(script, true);

            // 2. 执行计算
            Object result = compiledExp.execute(envContext);

            // 3. 结果转换：统一处理为 BigDecimal，并保留 2 位小数 (四舍五入)
            if (result instanceof BigDecimal) {
                return ((BigDecimal) result).setScale(2, RoundingMode.HALF_UP);
            } else if (result instanceof Number) {
                return new BigDecimal(result.toString()).setScale(2, RoundingMode.HALF_UP);
            } else {
                return BigDecimal.ZERO;
            }
        } catch (Exception e) {
            log.error("薪资公式执行失败! 脚本: [{}], 参数: {}", script, envContext, e);
            throw new BusinessException("公式计算异常: " + script + "，请检查变量是否缺失或语法错误。");
        }
    }
}