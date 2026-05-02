package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.common.BusinessException;
import com.example.demo.common.ResultCode;
import com.example.demo.context.UserContext;
import com.example.demo.entity.Article;
import com.example.demo.entity.ArticleTag;
import com.example.demo.entity.Tag;
import com.example.demo.entity.User;
import com.example.demo.enums.ArticleStatus;
import com.example.demo.mapper.ArticleMapper;
import com.example.demo.mapper.ArticleTagMapper;
import com.example.demo.mapper.TagMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.ArticleService;
import com.example.demo.vo.ArticleAddVo;
import com.example.demo.vo.ArticleResponseVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 文章服务实现类
 */
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleMapper articleMapper;
    private final ArticleTagMapper articleTagMapper;
    private final TagMapper tagMapper;
    private final UserMapper userMapper;


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
    public ArticleResponseVo getById(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "找不到文章");
        }

        List<ArticleTag> articleTags = articleTagMapper.selectList(new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getArticleId, id));
        List<Long> tagIds = articleTags.stream()
                .map(ArticleTag::getTagId)
                .toList();

        List<Tag> tags = tagIds.isEmpty() ? List.of() : tagMapper.selectByIds(tagIds);
        ArticleResponseVo articleResponse = new ArticleResponseVo();
        BeanUtils.copyProperties(article, articleResponse);
//        不是为了真的创建一个空数组来使用，而是作为“数组类型模板”，让 toArray 知道要返回 Tag[]。
        articleResponse.setTags(tags.toArray(new Tag[0]));

        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getId, article.getUserId()));
        articleResponse.setUserName(user.getUsername());

//        Article updateArticle = new Article();
//        updateArticle.setId(id);
//        updateArticle.setViews(article.getViews() + 1);
//        articleMapper.updateById(updateArticle);
        articleMapper.increaseViews(id);

        return articleResponse;
    }

}
