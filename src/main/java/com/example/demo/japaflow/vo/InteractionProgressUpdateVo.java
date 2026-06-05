package com.example.demo.japaflow.vo;

import lombok.Data;

import java.util.Map;

@Data
public class InteractionProgressUpdateVo {
    private String pronunciationState;
    private Boolean skipped;
    private Map<String, Object> detail;
}
