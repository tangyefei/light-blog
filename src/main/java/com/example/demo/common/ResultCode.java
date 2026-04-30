package com.example.demo.common;

import lombok.Getter;

/**
 * 统一响应状态码枚举
 */
@Getter
public enum ResultCode {

    // ==================== 成功 ====================
    SUCCESS(200, "success"),

    // ==================== 客户端错误 4xx ====================
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或 Token 已失效"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    CONFLICT(409, "资源已存在"),

    // ==================== 服务端错误 5xx ====================
    INTERNAL_ERROR(500, "服务器内部错误");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
