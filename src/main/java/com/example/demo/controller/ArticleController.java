package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.common.ResultCode;
import com.example.demo.service.ArticleService;
import com.example.demo.vo.ArticleAddVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 文章模块接口
 */
@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
@Tag(name = " 文章模块", description = "文章新增、修改、删除、查询关接口")
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping("/add")
    @Operation(summary = "新增文章", description = "新增文章")
    public Result<Long> add(@Valid @RequestBody ArticleAddVo request) {
        Long id = articleService.add(request);
        return Result.success(id);
    }
}
