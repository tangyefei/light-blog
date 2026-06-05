package com.example.demo.japaflow.controller;

import com.example.demo.common.Result;
import com.example.demo.context.UserContext;
import com.example.demo.japaflow.entity.JfExerciseResult;
import com.example.demo.japaflow.service.ExerciseService;
import com.example.demo.japaflow.vo.ExerciseSubmitVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/japaflow/lessons")
@RequiredArgsConstructor
@Tag(name = "JapaFlow - 练习题", description = "练习题答题结果读写")
public class JfExerciseController {

    private final ExerciseService service;

    @GetMapping("/{lessonId}/exercises")
    @Operation(summary = "查询某课练习结果")
    public Result<List<JfExerciseResult>> list(@PathVariable Integer lessonId) {
        return Result.success(service.listByLesson(UserContext.getUserId(), lessonId));
    }

    @PostMapping("/{lessonId}/exercises/{exerciseId}")
    @Operation(summary = "提交练习答案")
    public Result<JfExerciseResult> submit(@PathVariable Integer lessonId,
                                           @PathVariable String exerciseId,
                                           @RequestBody ExerciseSubmitVo vo) {
        return Result.success(service.submit(UserContext.getUserId(), lessonId, exerciseId, vo));
    }

    @DeleteMapping("/{lessonId}/exercises")
    @Operation(summary = "清空某课练习结果")
    public Result<Void> reset(@PathVariable Integer lessonId) {
        service.resetByLesson(UserContext.getUserId(), lessonId);
        return Result.success();
    }
}
