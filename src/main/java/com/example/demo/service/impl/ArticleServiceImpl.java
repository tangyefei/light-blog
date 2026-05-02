package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.common.BusinessException;
import com.example.demo.common.ResultCode;
import com.example.demo.context.UserContext;
import com.example.demo.entity.Article;
import com.example.demo.entity.ArticleTag;
import com.example.demo.enums.ArticleStatus;
import com.example.demo.mapper.ArticleMapper;
import com.example.demo.mapper.ArticleTagMapper;
import com.example.demo.service.ArticleService;
import com.example.demo.vo.ArticleAddVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 文章服务实现类
 */
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleMapper articleMapper;
    private final ArticleTagMapper articleTagMapper;


    @Override
    public Long add(ArticleAddVo request) {
        Article article = new Article();
        article.setTitle(request.getTitle());
        article.setStatus(ArticleStatus.fromCode(request.getStatus()));
        article.setSummary(request.getSummary());
        article.setContent(request.getContent());
        article.setCategoryId(request.getCategoryId());
        article.setUserId(UserContext.getUserId());
        articleMapper.insert(article);

        if (request.getTagIds() != null) {
            articleTagMapper.delete(new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getArticleId, article.getId()));
            for (Long tagId : request.getTagIds()) {
                articleTagMapper.insert(new ArticleTag(article.getId(), tagId));
            }
        }

        return article.getId();
    }

    @Override
    public Article getById(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "找不到文章");
        }
        return article;
    }
}
