package com.example.demo.japaflow.controller;

import com.example.demo.common.Result;
import com.example.demo.context.UserContext;
import com.example.demo.japaflow.entity.JfWrongBook;
import com.example.demo.japaflow.service.WrongBookService;
import com.example.demo.japaflow.vo.WrongBookAddVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/japaflow/lessons")
@RequiredArgsConstructor
@Tag(name = "JapaFlow - 错题集", description = "错题集管理")
public class JfWrongBookController {

    private final WrongBookService service;

    @GetMapping("/{lessonId}/wrong-book")
    @Operation(summary = "查询某课错题")
    public Result<List<JfWrongBook>> list(@PathVariable Integer lessonId) {
        return Result.success(service.listByLesson(UserContext.getUserId(), lessonId));
    }

    @PostMapping("/{lessonId}/wrong-book")
    @Operation(summary = "添加错题")
    public Result<JfWrongBook> add(@PathVariable Integer lessonId,
                                   @RequestBody @Valid WrongBookAddVo vo) {
        return Result.success(service.add(UserContext.getUserId(), lessonId, vo));
    }

    @PutMapping("/{lessonId}/wrong-book/{itemType}/{itemId}/resolve")
    @Operation(summary = "标记错题为已解决")
    public Result<Void> resolve(@PathVariable Integer lessonId,
                                @PathVariable String itemType,
                                @PathVariable String itemId) {
        service.resolve(UserContext.getUserId(), lessonId, itemType, itemId);
        return Result.success();
    }

    @DeleteMapping("/{lessonId}/wrong-book")
    @Operation(summary = "清空某课错题")
    public Result<Void> reset(@PathVariable Integer lessonId) {
        service.resetByLesson(UserContext.getUserId(), lessonId);
        return Result.success();
    }
}
