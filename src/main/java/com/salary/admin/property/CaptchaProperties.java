package com.salary.admin.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 验证码配置属性类
 */
@Data
@Component
@ConfigurationProperties(prefix = "salary.security.captcha")
public class CaptchaProperties {

    /**
     * 是否开启验证码功能，默认开启
     */
    private boolean enabled = true;

    /**
     * 验证码图片宽度
     */
    private int width = 111;

    /**
     * 验证码图片高度
     */
    private int height = 36;

    /**
     * 验证码字符长度
     */
    private int length = 4;

    /**
     * 验证码过期时间（单位：秒）
     */
    private long expireSeconds = 120L;
}