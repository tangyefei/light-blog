package com.example.demo.japaflow.controller;

import com.example.demo.common.Result;
import com.example.demo.context.UserContext;
import com.example.demo.japaflow.service.ProgressIoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/japaflow/progress")
@RequiredArgsConstructor
@Tag(name = "JapaFlow - 全量同步", description = "进度导出/导入")
public class JfProgressIoController {

    private final ProgressIoService service;

    @GetMapping("/export")
    @Operation(summary = "导出当前用户全量进度")
    public Result<Map<String, Object>> export() {
        return Result.success(service.exportAll(UserContext.getUserId()));
    }

    @PostMapping("/import")
    @Operation(summary = "导入全量进度（按课程全量覆盖）")
    public Result<Map<String, Object>> importProgress(@RequestBody Map<String, Object> payload) {
        int count = service.importAll(UserContext.getUserId(), payload);
        return Result.success(Collections.singletonMap("lessonsImported", count));
    }
}
