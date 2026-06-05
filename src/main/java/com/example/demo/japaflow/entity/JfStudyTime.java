package com.example.demo.japaflow.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName(value = "jf_study_time", autoResultMap = true)
public class JfStudyTime {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private Integer lessonId;
    private String module;

    private Long totalMs;
    private LocalDateTime lastStartedAt;
    private LocalDateTime lastActiveAt;
    private LocalDateTime updatedAt;
}
