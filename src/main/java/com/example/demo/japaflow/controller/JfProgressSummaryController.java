package com.example.demo.japaflow.controller;

import com.example.demo.common.Result;
import com.example.demo.context.UserContext;
import com.example.demo.japaflow.service.ProgressSummaryService;
import com.example.demo.japaflow.vo.ProgressSummaryVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/japaflow/progress")
@RequiredArgsConstructor
@Tag(name = "JapaFlow - 进度总览", description = "课程进度概览")
public class JfProgressSummaryController {

    private final ProgressSummaryService service;

    @GetMapping("/summary")
    @Operation(summary = "查询课程进度总览（lessonIds 逗号分隔，不传则返回全部）")
    public Result<List<ProgressSummaryVo>> summary(@RequestParam(required = false) String lessonIds) {
        List<Integer> ids = Collections.emptyList();
        if (lessonIds != null && !lessonIds.isBlank()) {
            ids = Arrays.stream(lessonIds.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        }
        return Result.success(service.summary(UserContext.getUserId(), ids));
    }
}
