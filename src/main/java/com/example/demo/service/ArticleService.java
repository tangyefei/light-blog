package com.example.demo.service;

import com.example.demo.vo.ArticleAddVo;
import com.example.demo.vo.ArticleResponseVo;

/**
 * 文章服务接口
 */
public interface ArticleService {

    /**
     * 新增文章
     *
     * @param request 新增文章请求参数
     * @return 新文章 ID
     */
    Long add(ArticleAddVo request);

    /**
     * 根据 ID 查询文章详情。
     *
     * @param id 文章 ID
     * @return 文章详情
     */
    ArticleResponseVo getById(Long id);
}
