package com.example.demo.japaflow.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "jf_sentence_practice", autoResultMap = true)
public class JfSentencePractice {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private Integer lessonId;
    private String sentenceId;

    private Boolean pronunciationPassed;
    private Integer pronunciationScore;
    private Integer accuracyScore;
    private Integer fluencyScore;
    private Integer completenessScore;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> pronunciationReasons;

    private String recognizedText;
    private Integer pronunciationAttempts;

    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
}
