package com.salary.admin.service;

import com.salary.admin.model.dto.captcha.CaptchaResDTO;

public interface ICaptchaService {
    /**
     *  生成验证码，返回 Base64 图片字符串
     * @return CaptchaResDTO 验证码包装类
     */
    CaptchaResDTO generateCaptcha();

    /**
     * 校验验证码 (内部会处理 Redis 查验、阅后即焚、异常抛出)
     * @param captchaId 验证码ID
     * @param code 用户输入的验证码
     */
    boolean validateCaptcha(String captchaId, String code);
}
