package com.salary.admin.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 枚举类
 */
@Getter
@AllArgsConstructor
public enum ApiResultCode implements IApiResult {

    SUCCESS("0", "操作成功"),
    FAILED("1", "操作失败"),


    BAD_REQUEST("400", "请求参数错误"),
    UNAUTHORIZED("401", "未授权或暂未登录或token已经过期"),
    FORBIDDEN("403", "没有相关权限禁止访问"),
    NOT_FOUND("404", "资源不存在"),
    CONFLICT("409", "数据重复，违反唯一约束，请检查输入"),
    INTERNAL_SERVER_ERROR("500", "系统内部错误");

    private final String code;
    private final String message;


}
