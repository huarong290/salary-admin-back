package com.salary.admin.exception;

import com.salary.admin.common.ApiResultCode;
import com.salary.admin.common.IApiResult;
import lombok.Getter;

/**
 * 基础异常类
 */
@Getter
public class BaseException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final String code;

    public BaseException(String code, String message) {
        super(message);
        this.code = code;
    }

    public BaseException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public BaseException(IApiResult iApiResult) {
        super(iApiResult.getMessage());
        this.code = iApiResult.getCode();
    }

    public BaseException(String message) {
        super(message);
        this.code = ApiResultCode.FAILED.getCode(); // 默认失败码
    }
}
