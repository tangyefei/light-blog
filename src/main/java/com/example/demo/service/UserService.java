package com.example.demo.service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.User;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户注册
     *
     * @param request 注册请求参数
     */
    void register(RegisterRequest request);

    /**
     * 用户登录
     *
     * @param request 登录请求参数
     * @return 登录响应（含 Token）
     */
    LoginResponse login(LoginRequest request);

    User getById(Long id);
}
