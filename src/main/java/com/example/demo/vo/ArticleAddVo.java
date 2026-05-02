package com.example.demo.vo;

import lombok.Getter;

@Getter
public class ArticleAddVo {
    private Long id;
    private String title;
    private String summary;
    private String content;
    private Long categoryId;
    private Integer status;
    private Long[] tagIds;
}
