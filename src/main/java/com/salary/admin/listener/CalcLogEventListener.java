package com.salary.admin.listener;

import com.salary.admin.event.CalcLogEvent;
import com.salary.admin.model.entity.salary.SalaryCalcLog;
import com.salary.admin.service.salary.ISalaryCalcLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 薪资计算日志异步监听器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CalcLogEventListener {

    private final ISalaryCalcLogService iSalaryCalcLogService;

    /**
     * 异步消费日志事件并落库
     * 使用 @Async 保证它在一个独立的后台线程运行，绝不卡顿算薪主流程
     */
    @Async
    @EventListener
    public void onCalcLogEvent(CalcLogEvent event) {
        SalaryCalcLog calcLog = event.getSource();
        try {
            iSalaryCalcLogService.save(calcLog);
            log.debug("📝 异步写入规则流水日志成功: [{}]", calcLog.getRuleCode());
        } catch (Exception e) {
            // 异常隔离：就算数据库瞬时抖动导致日志写失败了，也只打印错误，绝不能让整个应用报错抛出
            log.error("❌ 异步写入薪资计算日志失败! 规则: [{}], 原因: {}", calcLog.getRuleCode(), e.getMessage());
        }
    }
}
