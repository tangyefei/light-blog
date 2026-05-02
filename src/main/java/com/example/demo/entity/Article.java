package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.handlers.MybatisEnumTypeHandler;
import com.example.demo.enums.ArticleStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName(value = "article", autoResultMap = true)
public class Article {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String summary;
    private String content;
    private String contentHtml;
    private Long views;
    private Long categoryId;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @TableField(typeHandler = MybatisEnumTypeHandler.class)
    private ArticleStatus status;
    @TableField("is_deleted")
    @TableLogic(value = "0", delval = "1")
    private Boolean deleted;
}
