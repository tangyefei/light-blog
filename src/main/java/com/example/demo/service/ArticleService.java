package com.example.demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.vo.ArticleAddVo;
import com.example.demo.vo.ArticleQueryVo;
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
     * 分页查询文章列表。
     *
     * @param request 查询条件
     * @return 文章分页列表
     */
    IPage<ArticleResponseVo> page(ArticleQueryVo request);

    /**
     * 根据 ID 查询文章详情。
     *
     * @param id 文章 ID
     * @return 文章详情
     */
    ArticleResponseVo getById(Long id);
}
