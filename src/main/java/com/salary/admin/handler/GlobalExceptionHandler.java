package com.salary.admin.handler;

import com.salary.admin.common.ApiResult;
import com.salary.admin.common.ApiResultCode;
import com.salary.admin.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * 作用：
 * 1. 捕获 Controller 层抛出的所有异常
 * 2. 转换为统一 ApiResult 返回给前端
 * 3. 防止异常信息直接暴露给用户
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理【自定义业务异常】
     *
     * 使用场景：
     *  - 参数非法
     *  - 状态不允许
     *  - 权限不足
     *
     * 特点：
     *  - 异常是“预期内的”
     *  - 日志级别使用 warn
     */
    @ExceptionHandler(BaseException.class)
    public ApiResult<Void> handleBaseException(BaseException ex) {
        // 业务异常记录 warn 即可，无需堆栈
        log.warn("业务异常 code={}, message={}", ex.getCode(), ex.getMessage());
        return ApiResult.failResult(ex.getCode(), ex.getMessage());
    }

    /**
     * 处理 @Valid 校验失败异常 (@RequestBody 参数)
     *
     * 例如：
     *  public ApiResult<?> add(@Valid UserDTO dto)
     */

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResult<Void> handleValidException(MethodArgumentNotValidException ex) {
        String message = java.util.Optional.ofNullable(ex.getBindingResult().getFieldError())
                .map(org.springframework.validation.FieldError::getDefaultMessage)
                .orElse("参数校验失败");
        log.warn("参数校验异常(JSON): {}", message);
        return ApiResult.failResult(ApiResultCode.BAD_REQUEST.getCode(), message);
    }
    /**
     * 处理单个参数校验异常 (@RequestParam/@PathVariable)
     */
    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public ApiResult<Void> handleConstraintViolationException(jakarta.validation.ConstraintViolationException ex) {
        String message = ex.getMessage();
        log.warn("参数约束异常: {}", message);
        return ApiResult.failResult(ApiResultCode.BAD_REQUEST.getCode(), message);
    }
    /**
     * 处理普通参数绑定异常 处理单个参数校验异常 (@RequestParam/@PathVariable)
     *
     * 例如：
     *  Long id 传了 abc
     */
    @ExceptionHandler(BindException.class)
    public ApiResult<Void> handleBindException(BindException ex) {
        String message = ex.getFieldError().getDefaultMessage();
        return ApiResult.failResult(
                ApiResultCode.BAD_REQUEST.getCode(),
                message
        );
    }
    /**
     * 处理 Spring MVC 请求方式错误 (如 POST 接口用了 GET)
     */
    @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
    public ApiResult<Void> handleMethodNotSupported(org.springframework.web.HttpRequestMethodNotSupportedException ex) {
        return ApiResult.failResult(ApiResultCode.BAD_REQUEST.getCode(), "请求方式不支持: " + ex.getMethod());
    }
    /**
     * 处理 JSON 请求体读取异常 (JSON 格式错误或类型转换失败)
     */
    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ApiResult<Void> handleHttpMessageNotReadableException(org.springframework.http.converter.HttpMessageNotReadableException ex) {
        log.warn("请求体不可读: {}", ex.getMessage());
        return ApiResult.failResult(ApiResultCode.BAD_REQUEST.getCode(), "请求参数格式错误");
    }
    /**
     * 兜底异常（程序错误）
     *
     * 特点：
     *  - 一般是 BUG
     *  - 日志级别 ERROR
     *  - 前端不返回具体异常信息
     */
    @ExceptionHandler(Exception.class)
    public ApiResult<Void> handleException(Exception ex) {
        // 系统 BUG 必须记录完整堆栈
        log.error("系统异常", ex);
        return ApiResult.failResult(ApiResultCode.INTERNAL_SERVER_ERROR);
    }
}

