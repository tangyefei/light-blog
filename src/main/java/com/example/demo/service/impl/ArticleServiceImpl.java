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
import com.example.demo.vo.ArticleUpdateVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 文章服务实现类
 */
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private static final Duration ARTICLE_CACHE_TTL = Duration.ofMinutes(10);
    private static final String ARTICLE_DETAIL_CACHE_PREFIX = "article:detail:";

    private final ArticleMapper articleMapper;
    private final ArticleTagMapper articleTagMapper;
    private final TagMapper tagMapper;
    private final UserMapper userMapper;
    // 操作 Redis 字符串类型数据，用于按 key 读写文章详情缓存。
    private final StringRedisTemplate stringRedisTemplate;
    // 负责 Java 对象和 JSON 字符串之间的转换，缓存中存储的是 JSON。
    private final ObjectMapper objectMapper;


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
    @Transactional
    public void update(Long id, ArticleUpdateVo request) {
        Article originArticle = articleMapper.selectById(id);
        if (originArticle == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "文章不存在");
        }
        // originArticle.getUserId() != UserContext.getUserId() 比较的是 Long 对象引用地址，可能会为 false
        else if (!UserContext.getUserId().equals(originArticle.getUserId())) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权限修改文章");
        }

        Article article = new Article();
        article.setId(id);
        article.setTitle(request.getTitle());

        // 有值才更新，前端交互可能会不传（看前端设计）
        if (request.getStatus() != null) {
            article.setStatus(ArticleStatus.fromCode(request.getStatus()));
        }
        article.setSummary(request.getSummary());
        article.setContent(request.getContent());
        article.setCategoryId(request.getCategoryId());
        article.setUserId(UserContext.getUserId());
        articleMapper.updateById(article);

        // 用户不传是，不要做任何改动（看前端设计）
        if (request.getTagIds() != null) {
            articleTagMapper.delete(new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getArticleId, id));
            for(Long tagId : request.getTagIds()) {
                articleTagMapper.insert(new ArticleTag(article.getId(), tagId));
            }
        }

        deleteCacheKey(ARTICLE_DETAIL_CACHE_PREFIX + id);
    }

    @Override
    public void delete(Long id) {
        Article originArticle = articleMapper.selectById(id);
        if (originArticle == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "文章不存在");
        }
        // originArticle.getUserId() != UserContext.getUserId() 比较的是 Long 对象引用地址，可能会为 false
        else if (!Objects.equals(UserContext.getUserId(), originArticle.getUserId())) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权限删除文章");
        }
        int affectCount = articleMapper.deleteById(id);
        if (affectCount == 0) {
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "删除文章失败");
        }

        deleteCacheKey(ARTICLE_DETAIL_CACHE_PREFIX + id);
    }

    @Override
    public IPage<ArticleResponseVo> page(ArticleQueryVo request) {
        // 如果 tag 不空，查出所有的符合 tag 条件的  articleIds，并用作后续的检索条件
        Page<Article> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<Article> query = new LambdaQueryWrapper<Article>()
                // 标题有值时，按标题进行模糊查询。
                .like(StringUtils.hasText(request.getTitle()), Article::getTitle, request.getTitle())
                // 分类 ID 有值时，只查询该分类下的文章。
                .eq(request.getCategoryId() != null, Article::getCategoryId, request.getCategoryId())
                // 状态有值时，将前端状态值转换为枚举后再查询。
                .eq(request.getStatus() != null, Article::getStatus, request.getStatus() == null ? null : ArticleStatus.fromCode(request.getStatus()))
                .orderByDesc(Article::getCreatedAt);

        if (request.getTagId() != null) {
            List<Long> articleIds = articleTagMapper.selectList(new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getTagId, request.getTagId())).stream().map(ArticleTag::getArticleId).collect(Collectors.toList());
            query.in(Article::getId, articleIds);
        }
        Page<Article> articlePage = articleMapper.selectPage(page, query);
        return articlePage.convert(this::toArticleResponseVo);
    }

    @Override
    public IPage<ArticleResponseVo> pageCurrentUserArticles(ArticleQueryVo request) {
        Long currentUserId = UserContext.getUserId();
        if (currentUserId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "未登录");
        }

        Page<Article> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<Article> query = new LambdaQueryWrapper<Article>()
                .eq(true, Article::getUserId, UserContext.getUserId())
                .like(StringUtils.hasText(request.getTitle()), Article::getTitle, request.getTitle())
                // 分类 ID 有值时，只查询该分类下的文章。
                .eq(request.getCategoryId() != null, Article::getCategoryId, request.getCategoryId())
                // 状态有值时，将前端状态值转换为枚举后再查询。
                .eq(request.getStatus() != null, Article::getStatus, request.getStatus() == null ? null : ArticleStatus.fromCode(request.getStatus()))
                .orderByDesc(Article::getCreatedAt);

        Page<Article> articlePage = articleMapper.selectPage(page, query);
        return articlePage.convert(this::toArticleResponseVo);
    }

    @Override
    public ArticleResponseVo getById(Long id) {
        String cacheKey = ARTICLE_DETAIL_CACHE_PREFIX + id;
        ArticleResponseVo cachedArticle = getCache(cacheKey, ArticleResponseVo.class);
        if (cachedArticle != null) {
            articleMapper.increaseViews(id);
            return cachedArticle;
        }

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
        setCache(cacheKey, articleResponse);

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

    /**
     * 根据 key 从 Redis 读取缓存，并反序列化成指定类型。
     * 读取或反序列化失败时删除异常缓存并返回 null，让调用方回退到数据库查询。
     */
    private <T> T getCache(String key, Class<T> type) {
        try {
            // 根据缓存 key 从 Redis 中读取 JSON 字符串。
            // opsForValue() 表示操作 Redis 的 String 类型数据，对应 Redis 命令中的 GET/SET 这类 key-value 操作。
            String value = stringRedisTemplate.opsForValue().get(key);
            // Redis 中没有对应数据时，返回 null，调用方继续查数据库。
            if (!StringUtils.hasText(value)) {
                return null;
            }
            // 把 JSON 字符串反序列化成调用方指定的 Java 类型。
            return objectMapper.readValue(value, type);
        } catch (Exception e) {
            // 如果缓存数据格式异常或 Redis 读取异常，删除该 key，避免后续继续读到脏数据。
            deleteCacheKey(key);
            // 返回 null 表示缓存不可用，调用方会回退到数据库查询。
            return null;
        }
    }

    private void setCache(String key, Object value) {
        try {
            // 将 Java 对象序列化为 JSON 字符串，并写入 Redis，同时设置过期时间。
            // opsForValue() 表示操作 Redis 的 String 类型数据，这里相当于 SET key value EX ttl。
            stringRedisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(value), ARTICLE_CACHE_TTL);
        } catch (Exception ignored) {
            // 缓存写入失败不影响主流程，接口仍然返回数据库查询结果。
        }
    }

    private void deleteCacheKey(String key) {
        try {
            // 删除指定 key 的缓存，常用于文章更新或删除后清理旧详情数据。
            stringRedisTemplate.delete(key);
        } catch (Exception ignored) {
            // 缓存删除失败不影响文章更新或删除的主流程。
        }
    }

}
