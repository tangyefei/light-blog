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
@TableName(value = "jf_word_learning", autoResultMap = true)
public class JfWordLearning {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private Integer lessonId;
    private String wordId;

    private String mainStatus;
    private Boolean slashed;

    private Boolean meaningToWordCorrect;
    private Boolean audioToWordCorrect;
    private Boolean wordToMeaningCorrect;
    private Boolean pronunciationPassed;

    private Integer pronunciationScore;
    private Integer accuracyScore;
    private Integer fluencyScore;
    private Integer completenessScore;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> diagnosticTags;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> pronunciationReasons;

    private String recognizedText;

    private Integer attemptsMeaningToWord;
    private Integer attemptsAudioToWord;
    private Integer attemptsWordToMeaning;
    private Integer attemptsPronunciation;

    private LocalDateTime lastPracticedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
