package com.salary.admin.exception;

import com.salary.admin.common.ApiResultCode;
import com.salary.admin.common.IApiResult;

/**
 * 业务异常类
 * 用于抛出明确的业务逻辑错误，比如参数非法、状态不允许等
 */
public class BusinessException extends BaseException {

    private static final long serialVersionUID = 1L;

    /**
     * 使用错误码和消息构造业务异常
     */
    public BusinessException(String code, String message) {

        super(code, message);
    }

    /**
     * 使用错误码、消息和原始异常构造业务异常
     */
    public BusinessException(String code, String message, Throwable cause) {

        super(code, message, cause);
    }

    /**
     * 使用统一的 IApiResult 枚举构造业务异常
     */
    public BusinessException(IApiResult iApiResult) {

        super(iApiResult);
    }

    /**
     * 只传入消息，默认使用业务失败码
     */
    public BusinessException(String message) {

        super(ApiResultCode.FAILED.getCode(), message);
    }
}
