package com.example.demo.japaflow.controller;

import com.example.demo.common.Result;
import com.example.demo.context.UserContext;
import com.example.demo.japaflow.service.WordLearningService;
import com.example.demo.japaflow.vo.WordLearningResponseVo;
import com.example.demo.japaflow.vo.WordLearningUpdateVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/japaflow/lessons")
@RequiredArgsConstructor
@Tag(name = "JapaFlow - 单词学习", description = "单词学习状态读写")
public class JfWordLearningController {

    private final WordLearningService service;

    @GetMapping("/{lessonId}/words")
    @Operation(summary = "查询某课全部单词学习状态")
    public Result<List<WordLearningResponseVo>> list(@PathVariable Integer lessonId) {
        return Result.success(service.listByLesson(UserContext.getUserId(), lessonId));
    }

    @PutMapping("/{lessonId}/words/{wordId}")
    @Operation(summary = "更新单个单词学习状态（部分更新）")
    public Result<WordLearningResponseVo> update(@PathVariable Integer lessonId,
                                                 @PathVariable String wordId,
                                                 @RequestBody WordLearningUpdateVo vo) {
        return Result.success(service.upsert(UserContext.getUserId(), lessonId, wordId, vo));
    }

    @DeleteMapping("/{lessonId}/words")
    @Operation(summary = "重置某课单词学习数据")
    public Result<Void> reset(@PathVariable Integer lessonId) {
        service.resetByLesson(UserContext.getUserId(), lessonId);
        return Result.success();
    }
}
