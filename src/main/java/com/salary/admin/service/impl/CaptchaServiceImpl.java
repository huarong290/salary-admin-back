package com.salary.admin.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.salary.admin.constants.redis.RedisCacheConstants;
import com.salary.admin.exception.BusinessException;
import com.salary.admin.model.dto.captcha.CaptchaResDTO;
import com.salary.admin.property.CaptchaProperties;
import com.salary.admin.service.ICaptchaService;
import com.salary.admin.service.IRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 验证码服务实现类
 * - 负责生成验证码图片、存储到 Redis、校验验证码、删除验证码。
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CaptchaServiceImpl implements ICaptchaService {
    private final IRedisService iRedisService;
    private final CaptchaProperties captchaProperties;
    @Override
    public CaptchaResDTO generateCaptcha() {
        // 1. 判断开关，如果关闭则通知前端隐藏验证码组件
        if (!captchaProperties.isEnabled()) {
            log.debug("验证码功能已关闭，跳过生成");
            return CaptchaResDTO.builder()
                    .captchaEnabled(false)
                    .build();
        }

        // 2. 从配置类动态读取参数，生成字符+干扰线验证码图片
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(
                captchaProperties.getWidth(),
                captchaProperties.getHeight(),
                captchaProperties.getLength(),
                10
        );

        // 3. 随机生成 16 位 captchaId
        String captchaId = RandomStringUtils.randomAlphanumeric(16);
        String verifyKey = RedisCacheConstants.AUTH_CAPTCHA + captchaId;

        // 4. 获取验证码文本和图片 Base64
        String captchaCode = lineCaptcha.getCode();

        // 5. 存入 Redis，从配置类读取过期时间
        iRedisService.setEx(verifyKey, captchaCode, captchaProperties.getExpireSeconds(), TimeUnit.SECONDS);

        // 6. 组装并返回 DTO
        return CaptchaResDTO.builder()
                .captchaId(captchaId)
                .captchaImage(lineCaptcha.getImageBase64Data())
                .expireTime(System.currentTimeMillis() + captchaProperties.getExpireSeconds() * 1000)
                .captchaEnabled(true)
                .build();
    }

    @Override
    public boolean validateCaptcha(String captchaId, String code) {
        // 1. 如果开关没开，直接放行，相当于校验成功
        if (!captchaProperties.isEnabled()) {
            return true;
        }

        // 2. 基础参数校验
        if (StringUtils.isBlank(captchaId) || StringUtils.isBlank(code)) {
            throw new BusinessException("验证码不能为空");
        }

        String verifyKey = RedisCacheConstants.AUTH_CAPTCHA + captchaId;
        String redisCode = iRedisService.get(verifyKey, String.class);

        // 3. 核心安全策略：阅后即焚，防止重复提交和暴力破解
        iRedisService.del(verifyKey);

        // 4. 校验是否过期
        if (StringUtils.isBlank(redisCode)) {
            throw new BusinessException("验证码已过期，请刷新重试");
        }

        // 5. 校验内容 (忽略大小写)
        if (!redisCode.equalsIgnoreCase(code)) {
            throw new BusinessException("验证码错误");
        }

        // 6. 全部校验通过
        return true;
    }
}
