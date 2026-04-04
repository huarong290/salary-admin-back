package com.salary.admin.event;


import com.salary.admin.model.entity.salary.SalaryCalcLog;
import org.springframework.context.ApplicationEvent;

/**
 * 薪资计算节点日志事件
 */
public class CalcLogEvent extends ApplicationEvent {

    public CalcLogEvent(SalaryCalcLog source) {
        super(source);
    }

    @Override
    public SalaryCalcLog getSource() {
        return (SalaryCalcLog) super.getSource();
    }
}