package com.example.demo.japaflow.controller;

import com.example.demo.common.Result;
import com.example.demo.context.UserContext;
import com.example.demo.japaflow.entity.JfLessonPreference;
import com.example.demo.japaflow.service.LessonPreferenceService;
import com.example.demo.japaflow.vo.LessonPreferenceUpdateVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/japaflow/lessons")
@RequiredArgsConstructor
@Tag(name = "JapaFlow - 课程偏好", description = "播放速度、语音、tab 等偏好设置")
public class JfLessonPreferenceController {

    private final LessonPreferenceService service;

    @GetMapping("/{lessonId}/preferences")
    @Operation(summary = "查询课程偏好")
    public Result<JfLessonPreference> get(@PathVariable Integer lessonId) {
        return Result.success(service.get(UserContext.getUserId(), lessonId));
    }

    @PutMapping("/{lessonId}/preferences")
    @Operation(summary = "更新课程偏好（部分字段）")
    public Result<JfLessonPreference> update(@PathVariable Integer lessonId,
                                             @RequestBody LessonPreferenceUpdateVo vo) {
        return Result.success(service.upsert(UserContext.getUserId(), lessonId, vo));
    }
}
