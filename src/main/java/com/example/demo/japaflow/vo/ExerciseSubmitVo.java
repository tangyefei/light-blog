package com.example.demo.japaflow.vo;

import lombok.Data;

@Data
public class ExerciseSubmitVo {
    private String groupId;
    private String userAnswer;
    private Boolean isCorrect;
    private Boolean isSkipped;
}
