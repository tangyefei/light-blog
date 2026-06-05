package com.example.demo.japaflow.vo;

import lombok.Data;

import java.util.List;

@Data
public class SentencePracticeUpdateVo {
    private Boolean pronunciationPassed;
    private Integer pronunciationScore;
    private Integer accuracyScore;
    private Integer fluencyScore;
    private Integer completenessScore;
    private List<String> pronunciationReasons;
    private String recognizedText;
    private Integer pronunciationAttempts;
}
