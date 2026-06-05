package com.example.demo.japaflow.controller;

import com.example.demo.common.Result;
import com.example.demo.context.UserContext;
import com.example.demo.japaflow.entity.JfGrammarPractice;
import com.example.demo.japaflow.service.GrammarPracticeService;
import com.example.demo.japaflow.vo.GrammarPracticeUpdateVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/japaflow/lessons")
@RequiredArgsConstructor
@Tag(name = "JapaFlow - 语法练习", description = "语法例句练习读写")
public class JfGrammarPracticeController {

    private final GrammarPracticeService service;

    @GetMapping("/{lessonId}/grammar")
    @Operation(summary = "查询某课语法练习状态")
    public Result<List<JfGrammarPractice>> list(@PathVariable Integer lessonId) {
        return Result.success(service.listByLesson(UserContext.getUserId(), lessonId));
    }

    @PutMapping("/{lessonId}/grammar/{grammarId}/{exampleIndex}")
    @Operation(summary = "更新语法例句练习状态")
    public Result<JfGrammarPractice> update(@PathVariable Integer lessonId,
                                            @PathVariable String grammarId,
                                            @PathVariable Integer exampleIndex,
                                            @RequestBody GrammarPracticeUpdateVo vo) {
        return Result.success(service.upsert(UserContext.getUserId(), lessonId, grammarId, exampleIndex, vo));
    }
}
