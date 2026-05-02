package com.example.demo.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * 文章分页查询请求参数。
 */
@Getter
@Setter
public class ArticleQueryVo {

    /**
     * 当前页码，从 1 开始。
     */
    private Long pageNum = 1L;

    /**
     * 每页条数。
     */
    private Long pageSize = 10L;

    /**
     * 文章标题关键字。
     */
    private String title;

    /**
     * 分类 ID。
     */
    private Long categoryId;

    /**
     * 标签 ID。
     */
    private Long tagId;

    /**
     * 文章状态。
     */
    private Integer status;
}
