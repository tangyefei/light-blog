package com.example.demo.japaflow.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StudyTimeIncrementVo {
    @NotNull(message = "deltaMs 不能为空")
    private Long deltaMs;

    private LocalDateTime activeAt;
}
