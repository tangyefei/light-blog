package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.Article;
import com.example.demo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 用户 Mapper 接口
 * 继承 BaseMapper 获得基础 CRUD 能力
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
    @Update("UPDATE article SET views = views + 1 WHERE id = #{id} AND is_deleted = 0")
    int increaseViews(@Param("id") Long id);
}
