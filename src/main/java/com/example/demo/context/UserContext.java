package com.example.demo.context;

/**
 * 基于 ThreadLocal 的轻量级用户上下文。
 *
 * 为什么需要？
 * 拦截器把当前登录用户的信息存在这里，后续 Controller、Service 可以随时取出，
 * 而不需要把 user 对象一层层传参。
 *
 * 关键知识点：
 * - ThreadLocal 为每个线程提供了独立的副本，天然线程安全。
 * - 请求结束后必须调用 clear() 清空，否则在线程池复用场景下会造成内存泄漏或数据串用。
 */
public class UserContext {
    private static final ThreadLocal<Long> userId = new ThreadLocal<>();
    private static final ThreadLocal<String> username = new ThreadLocal<>();

    public static void setUserId(Long id) {
        userId.set(id);
    }
    public static void setUsername(String name) {
        username.set(name);
    }

    public static Long getUserId() {
        return userId.get();
    }

    public static String getUsername() {
        return username.get();
    }

    public static void clear() {
        userId.remove();
        username.remove();
    }
}
