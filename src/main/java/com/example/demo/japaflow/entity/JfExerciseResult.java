package com.example.demo.japaflow.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName(value = "jf_exercise_result", autoResultMap = true)
public class JfExerciseResult {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private Integer lessonId;
    private String exerciseId;
    private String groupId;
    private Integer groupIndex;

    private String answer;
    private Boolean correct;
    private Boolean isSkipped;
    private LocalDateTime submittedAt;
}
