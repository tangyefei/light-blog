package com.example.demo.vo;

import com.example.demo.entity.Article;
import com.example.demo.entity.Tag;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文章查询返回对象。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ArticleResponseVo extends Article {

    private Tag[] tags;
    private String userName;
}
