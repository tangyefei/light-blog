package com.example.demo.common;

import lombok.Getter;

/**
 * 业务异常，由 GlobalExceptionHandler 统一捕获并返回 Result
 */
@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 使用 ResultCode 枚举 + 枚举默认 message
     */
    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    /**
     * 使用 ResultCode 枚举 + 自定义 message
     */
    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
    }
}
