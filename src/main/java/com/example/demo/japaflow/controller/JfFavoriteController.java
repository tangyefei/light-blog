package com.example.demo.japaflow.controller;

import com.example.demo.common.Result;
import com.example.demo.context.UserContext;
import com.example.demo.japaflow.entity.JfFavorite;
import com.example.demo.japaflow.service.FavoriteService;
import com.example.demo.japaflow.vo.FavoriteAddVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/japaflow")
@RequiredArgsConstructor
@Tag(name = "JapaFlow - 收藏", description = "单词/句子收藏管理")
public class JfFavoriteController {

    private final FavoriteService service;

    @GetMapping("/favorites")
    @Operation(summary = "查询收藏（lessonId 可选）")
    public Result<List<JfFavorite>> list(@RequestParam(required = false) Integer lessonId) {
        return Result.success(service.list(UserContext.getUserId(), lessonId));
    }

    @PostMapping("/lessons/{lessonId}/favorites")
    @Operation(summary = "添加收藏")
    public Result<JfFavorite> add(@PathVariable Integer lessonId,
                                  @RequestBody @Valid FavoriteAddVo vo) {
        return Result.success(service.add(UserContext.getUserId(), lessonId, vo));
    }

    @DeleteMapping("/lessons/{lessonId}/favorites/{itemType}/{itemId}")
    @Operation(summary = "取消收藏")
    public Result<Void> remove(@PathVariable Integer lessonId,
                               @PathVariable String itemType,
                               @PathVariable String itemId) {
        service.remove(UserContext.getUserId(), lessonId, itemType, itemId);
        return Result.success();
    }
}
