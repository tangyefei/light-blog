package com.example.demo.common;

import lombok.Getter;

/**
 * 统一响应结果封装
 *
 * <p>使用 @Getter 而非 @Data，避免生成 setter，保证响应对象不可变</p>
 *
 * <p>使用示例：
 * <pre>
 *   Result.success(data)                    // 200 + data
 *   Result.success()                        // 200 + null
 *   Result.fail("用户名已存在")              // 400 + message
 *   Result.fail(ResultCode.UNAUTHORIZED)    // 401 + 默认 message
 *   Result.fail(ResultCode.NOT_FOUND, "用户不存在")  // 404 + 自定义 message
 * </pre>
 * </p>
 */
@Getter
public class Result<T> {

    private final Integer code;
    private final String message;
    private final T data;

    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // ==================== 成功响应 ====================

    /**
     * 成功，携带数据
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    /**
     * 成功，无数据（如注册、删除等操作）
     */
    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null);
    }

    // ==================== 失败响应 ====================

    /**
     * 失败，使用默认 400 状态码，自定义 message
     */
    public static <T> Result<T> fail(String message) {
        return new Result<>(ResultCode.BAD_REQUEST.getCode(), message, null);
    }

    /**
     * 失败，使用枚举状态码（code + 枚举默认 message）
     */
    public static <T> Result<T> fail(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage(), null);
    }

    /**
     * 失败，使用枚举状态码 + 自定义 message（覆盖枚举默认 message）
     */
    public static <T> Result<T> fail(ResultCode resultCode, String message) {
        return new Result<>(resultCode.getCode(), message, null);
    }

    /**
     * 失败，完全自定义 code + message（兜底方法，优先使用枚举重载）
     */
    public static <T> Result<T> fail(Integer code, String message) {
        return new Result<>(code, message, null);
    }
}
