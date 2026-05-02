package com.example.demo.vo;

import lombok.Getter;

/**
 * 修改文章请求参数。
 */
@Getter
public class ArticleUpdateVo {

    private String title;

    private String summary;

    private String content;

    private Long categoryId;

    private Integer status;

    private Long[] tagIds;
}
