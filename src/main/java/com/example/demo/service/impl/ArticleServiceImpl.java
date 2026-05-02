package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import com.example.demo.vo.ArticleQueryVo;
import com.example.demo.vo.ArticleResponseVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
    public IPage<ArticleResponseVo> page(ArticleQueryVo request) {
        Page<Article> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<Article> query = new LambdaQueryWrapper<Article>()
                // 标题有值时，按标题进行模糊查询。
                .like(StringUtils.hasText(request.getTitle()), Article::getTitle, request.getTitle())
                // 分类 ID 有值时，只查询该分类下的文章。
                .eq(request.getCategoryId() != null, Article::getCategoryId, request.getCategoryId())
                .eq(request.getTagId() != null, Article::getId, request.getTagId())
                // 状态有值时，将前端状态值转换为枚举后再查询。
                .eq(request.getStatus() != null, Article::getStatus, request.getStatus() == null ? null : ArticleStatus.fromCode(request.getStatus()))
                .orderByDesc(Article::getCreatedAt);

        Page<Article> articlePage = articleMapper.selectPage(page, query);
        return articlePage.convert(this::toArticleResponseVo);
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

        articleMapper.increaseViews(id);

        return articleResponse;
    }

    private ArticleResponseVo toArticleResponseVo(Article article) {
        ArticleResponseVo articleResponse = new ArticleResponseVo();
        BeanUtils.copyProperties(article, articleResponse);

        List<ArticleTag> articleTags = articleTagMapper.selectList(
                new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getArticleId, article.getId())
        );
        List<Long> tagIds = articleTags.stream()
                .map(ArticleTag::getTagId)
                .toList();
        List<Tag> tags = tagIds.isEmpty() ? List.of() : tagMapper.selectByIds(tagIds);
        articleResponse.setTags(tags.toArray(new Tag[0]));

        User user = userMapper.selectById(article.getUserId());
        if (user != null) {
            articleResponse.setUserName(user.getUsername());
        }

        return articleResponse;
    }

}
