package com.example.demo.vo;

import lombok.Data;

@Data
public class LoginParams {
    private String account; // 账号，支持用户名或邮箱登录
    private String password;
}
