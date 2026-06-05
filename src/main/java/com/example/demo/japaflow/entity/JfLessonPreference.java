package com.example.demo.japaflow.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName(value = "jf_lesson_preference", autoResultMap = true)
public class JfLessonPreference {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private Integer lessonId;

    private String currentVoiceId;
    private BigDecimal playbackRate;
    private Boolean vocabFocusOnly;
    private Integer currentExerciseGroup;
    private String textCurrentTab;

    private LocalDateTime updatedAt;
}
