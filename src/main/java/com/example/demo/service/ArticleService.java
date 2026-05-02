package com.example.demo.service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.vo.ArticleAddVo;

/**
 * 用户服务接口
 */
public interface ArticleService {

    /**
     * 用户注册
     *
     * @param request 注册请求参数
     */
    Long add(ArticleAddVo request);

}
