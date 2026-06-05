package com.example.demo.japaflow.vo;

import lombok.Data;

@Data
public class ProgressSummaryVo {
    private Integer lessonId;
    private CountVo vocab;
    private CountVo grammar;
    private CountVo text;
    private CountVo exercises;
    private CountVo weak;
    private Integer percent;
    private String status;
    private Long totalStudyTimeMs;

    @Data
    public static class CountVo {
        private Integer completed;
        private Integer total;

        public CountVo() {}
        public CountVo(Integer completed, Integer total) {
            this.completed = completed;
            this.total = total;
        }
    }
}
