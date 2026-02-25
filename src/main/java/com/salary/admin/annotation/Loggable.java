package com.salary.admin.annotation;

import java.lang.annotation.*;

/**
 * 标记需要记录日志的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Loggable {
    /**
     * 模块名称
     */
    String title() default "";
    /**
     * 业务类型 (0-其它, 1-新增, 2-修改...)
     */
    int businessType() default 0;
    /**
     * 是否打印返回值
     */
    boolean logResponse() default true;

    /**
     * 是否打印入参
     */
    boolean logRequest() default true;

    /**
     * 新增：安全序列化
     */
    boolean safeSerialize() default true;
}
