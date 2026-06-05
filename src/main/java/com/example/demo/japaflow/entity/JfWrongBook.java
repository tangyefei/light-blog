package com.example.demo.japaflow.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@TableName(value = "jf_wrong_book", autoResultMap = true)
public class JfWrongBook {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private Integer lessonId;
    private String itemType;
    private String itemId;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> wrongDetail;

    private Boolean resolved;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}
