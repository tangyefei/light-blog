package com.example.demo.japaflow.service;

import com.example.demo.japaflow.vo.ProgressSummaryVo;

import java.util.List;

public interface ProgressSummaryService {
    /** 不传 lessonIds 时返回该用户所有有记录的课程进度。 */
    List<ProgressSummaryVo> summary(Long userId, List<Integer> lessonIds);
}
