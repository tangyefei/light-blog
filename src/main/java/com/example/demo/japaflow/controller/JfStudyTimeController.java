package com.example.demo.japaflow.controller;

import com.example.demo.common.Result;
import com.example.demo.context.UserContext;
import com.example.demo.japaflow.entity.JfStudyTime;
import com.example.demo.japaflow.service.StudyTimeService;
import com.example.demo.japaflow.vo.StudyTimeIncrementVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/japaflow/lessons")
@RequiredArgsConstructor
@Tag(name = "JapaFlow - 学习时长", description = "各模块学习时长统计")
public class JfStudyTimeController {

    private final StudyTimeService service;

    @GetMapping("/{lessonId}/study-time")
    @Operation(summary = "查询某课各模块学习时长")
    public Result<Map<String, Map<String, Object>>> get(@PathVariable Integer lessonId) {
        return Result.success(service.getByLesson(UserContext.getUserId(), lessonId));
    }

    @PostMapping("/{lessonId}/study-time/{module}")
    @Operation(summary = "累加学习时长（增量）")
    public Result<JfStudyTime> increment(@PathVariable Integer lessonId,
                                         @PathVariable String module,
                                         @RequestBody @Valid StudyTimeIncrementVo vo) {
        return Result.success(service.increment(UserContext.getUserId(), lessonId, module, vo));
    }
}
