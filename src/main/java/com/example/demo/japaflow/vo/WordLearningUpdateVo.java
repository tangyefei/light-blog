package com.example.demo.japaflow.vo;

import lombok.Data;

import java.util.List;

/**
 * 单词学习状态更新请求（部分字段）。null 表示不更新该字段。
 */
@Data
public class WordLearningUpdateVo {
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

    @Data
    public static class AttemptsVo {
        private Integer meaningToWord;
        private Integer audioToWord;
        private Integer wordToMeaning;
        private Integer pronunciation;
    }
}
