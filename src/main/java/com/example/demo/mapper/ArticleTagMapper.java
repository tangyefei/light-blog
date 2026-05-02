package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.Article;
import com.example.demo.entity.ArticleTag;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper 接口
 * 继承 BaseMapper 获得基础 CRUD 能力
 */
@Mapper
public interface ArticleTagMapper extends BaseMapper<ArticleTag> {
}
