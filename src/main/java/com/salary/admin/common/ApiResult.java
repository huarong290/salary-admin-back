package com.salary.admin.common;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 统一响应结果封装
 * 使用 JDK 21 Record 特性，代码简洁且线程安全
 *
 * @param <T> 响应数据类型
 */
@Schema(description = "统一响应结果")
public record ApiResult<T>(
        @Schema(description = "状态码", example = "0")
        String code,
        @Schema(description = "响应消息", example = "操作成功")
        String message,
        @Schema(description = "响应数据")
        T data,
        @Schema(description = "时间戳", example = "1700000000000")
        long timestamp
) implements Serializable {
    // ============================ 成功快捷返回 ============================

    /**
     * 默认成功返回
     *
     * @param <T>
     * @return
     */
    public static <T> ApiResult<T> defaultSuccessResult() {
        return buildResult(ApiResultCode.SUCCESS.getCode(), ApiResultCode.SUCCESS.getMessage(), null);
    }

    public static <T> ApiResult<T> successResult(T data) {
        return buildResult(ApiResultCode.SUCCESS.getCode(), ApiResultCode.SUCCESS.getMessage(), data);
    }

    public static <T> ApiResult<T> successResult(String message, T data) {
        return buildResult(ApiResultCode.SUCCESS.getCode(), message, data);
    }

    public static <T> ApiResult<T> successResult(IApiResult iApiResult, T data) {
        return buildResult(iApiResult.getCode(), iApiResult.getMessage(), data);
    }
    // ============================ 失败快捷返回 ============================
    public static <T> ApiResult<T> defaultFailResult() {
            return failResult(ApiResultCode.FAILED.getMessage());
    }

        public static <T> ApiResult<T> failResult(String message) {
                return buildResult(ApiResultCode.FAILED.getCode(), message, null);
        }

        public static <T> ApiResult<T> failResult(String code, String message) {
                return buildResult(code, message, null);
        }

        public static <T> ApiResult<T> failResult(IApiResult iApiResult) {
                return buildResult(iApiResult.getCode(), iApiResult.getMessage(), null);
        }

        public static <T> ApiResult<T> failResult(IApiResult iApiResult, String message) {
                return buildResult(iApiResult.getCode(), message, null);
        }

        public static <T> ApiResult<T> fail(T data) {
                return buildResult(ApiResultCode.FAILED.getCode(), ApiResultCode.FAILED.getMessage(), data);
        }
    // ============================ 核心构建与工具 ============================


    /**
     * 构造统一返回结果
     * 核心构建方法：Record 没有 setter，必须通过全参构造器 new 出新对象
     */
    private static <T> ApiResult<T> buildResult(String code, String message, T data) {
        return new ApiResult<>(code, message, data, System.currentTimeMillis());
    }

    /**
     * 转换为 Map：常用于 Security Filter 层直接输出 JSON
     */
    public static Map<String, Object> toMap(String code, String message, Object data) {
        Map<String, Object> map = new HashMap<>(4);
        map.put("code", code);
        map.put("message", message);
        map.put("data", data);
        map.put("timestamp", System.currentTimeMillis());
        return map;
    }
}
