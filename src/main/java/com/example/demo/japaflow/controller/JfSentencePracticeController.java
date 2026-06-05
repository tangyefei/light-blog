package com.example.demo.japaflow.controller;

import com.example.demo.common.Result;
import com.example.demo.context.UserContext;
import com.example.demo.japaflow.entity.JfSentencePractice;
import com.example.demo.japaflow.service.SentencePracticeService;
import com.example.demo.japaflow.vo.SentencePracticeUpdateVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/japaflow/lessons")
@RequiredArgsConstructor
@Tag(name = "JapaFlow - 课文句子", description = "课文句子练习读写")
public class JfSentencePracticeController {

    private final SentencePracticeService service;

    @GetMapping("/{lessonId}/sentences")
    @Operation(summary = "查询某课句子练习状态")
    public Result<List<JfSentencePractice>> list(@PathVariable Integer lessonId) {
        return Result.success(service.listByLesson(UserContext.getUserId(), lessonId));
    }

    @PutMapping("/{lessonId}/sentences/{sentenceId}")
    @Operation(summary = "更新句子练习状态")
    public Result<JfSentencePractice> update(@PathVariable Integer lessonId,
                                             @PathVariable String sentenceId,
                                             @RequestBody SentencePracticeUpdateVo vo) {
        return Result.success(service.upsert(UserContext.getUserId(), lessonId, sentenceId, vo));
    }
}
