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
@TableName(value = "jf_grammar_practice", autoResultMap = true)
public class JfGrammarPractice {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private Integer lessonId;
    private String grammarId;
    private Integer exampleIndex;

    private String answer;
    private Boolean submitted;
    private Boolean correct;
    private Boolean revealed;
    private Integer attempts;

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
