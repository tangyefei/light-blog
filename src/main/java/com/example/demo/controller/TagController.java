package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.Tag;
import com.example.demo.service.TagService;
import com.example.demo.vo.TagAddVo;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 标签模块接口。
 */
@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "标签模块", description = "标签新增、查询相关接口")
public class TagController {

    private final TagService tagService;

    @GetMapping
    @Operation(summary = "查询全部标签", description = "获取所有文章标签")
    public Result<List<Tag>> findAll() {
        List<Tag> tags = tagService.findAll();
        return Result.success(tags);
    }

    @PostMapping("/add")
    @Operation(summary = "新增标签", description = "新增文章标签，标签名称不可重复")
    public Result<Long> add(@Valid @RequestBody TagAddVo request) {
        Long id = tagService.add(request);
        return Result.success(id);
    }
}
