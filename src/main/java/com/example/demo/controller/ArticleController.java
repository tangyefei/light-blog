package com.example.demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.common.Result;
import com.example.demo.service.ArticleService;
import com.example.demo.vo.ArticleAddVo;
import com.example.demo.vo.ArticleQueryVo;
import com.example.demo.vo.ArticleResponseVo;
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

    @GetMapping("/page")
    @Operation(summary = "分页查询文章列表", description = "按条件分页查询文章列表")
    public Result<IPage<ArticleResponseVo>> page(@Valid ArticleQueryVo request) {
        IPage<ArticleResponseVo> page = articleService.page(request);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询文章详情", description = "根据文章 ID 查询文章详情")
    public Result<ArticleResponseVo> getById(@PathVariable Long id) {
        ArticleResponseVo article = articleService.getById(id);
        return Result.success(article);
    }
}
