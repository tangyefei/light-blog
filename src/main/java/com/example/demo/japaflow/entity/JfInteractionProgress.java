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
@TableName(value = "jf_interaction_progress", autoResultMap = true)
public class JfInteractionProgress {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private Integer lessonId;
    private String itemType;
    private String itemId;

    private String pronunciationState;
    private Boolean skipped;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> detail;

    private LocalDateTime updatedAt;
}
