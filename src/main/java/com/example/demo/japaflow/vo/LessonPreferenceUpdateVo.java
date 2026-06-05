package com.example.demo.japaflow.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LessonPreferenceUpdateVo {
    private String currentVoiceId;
    private BigDecimal playbackRate;
    private Boolean vocabFocusOnly;
    private Integer currentExerciseGroup;
    private String textCurrentTab;
}
