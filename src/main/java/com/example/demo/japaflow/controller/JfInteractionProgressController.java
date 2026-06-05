package com.example.demo.japaflow.controller;

import com.example.demo.common.Result;
import com.example.demo.context.UserContext;
import com.example.demo.japaflow.entity.JfInteractionProgress;
import com.example.demo.japaflow.service.InteractionProgressService;
import com.example.demo.japaflow.vo.InteractionProgressUpdateVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/japaflow/lessons")
@RequiredArgsConstructor
@Tag(name = "JapaFlow - 交互进度", description = "跟读交互状态")
public class JfInteractionProgressController {

    private final InteractionProgressService service;

    @GetMapping("/{lessonId}/interaction-progress")
    @Operation(summary = "查询某课交互进度（按 type 分组）")
    public Result<Map<String, Map<String, Map<String, Object>>>> get(@PathVariable Integer lessonId) {
        return Result.success(service.listGrouped(UserContext.getUserId(), lessonId));
    }

    @PutMapping("/{lessonId}/interaction-progress/{itemType}/{itemId}")
    @Operation(summary = "更新交互进度项")
    public Result<JfInteractionProgress> update(@PathVariable Integer lessonId,
                                                @PathVariable String itemType,
                                                @PathVariable String itemId,
                                                @RequestBody InteractionProgressUpdateVo vo) {
        return Result.success(service.upsert(UserContext.getUserId(), lessonId, itemType, itemId, vo));
    }
}
