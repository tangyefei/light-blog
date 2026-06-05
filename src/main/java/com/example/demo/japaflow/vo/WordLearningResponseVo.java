package com.example.demo.japaflow.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class WordLearningResponseVo {
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

    private List<String> diagnosticTags;
    private List<String> pronunciationReasons;
    private String recognizedText;

    private AttemptsVo attempts;
    private LocalDateTime lastPracticedAt;

    @Data
    public static class AttemptsVo {
        private Integer meaningToWord;
        private Integer audioToWord;
        private Integer wordToMeaning;
        private Integer pronunciation;
    }
}
