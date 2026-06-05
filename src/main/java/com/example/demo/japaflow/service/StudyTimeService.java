package com.example.demo.japaflow.service;

import com.example.demo.japaflow.entity.JfStudyTime;
import com.example.demo.japaflow.vo.StudyTimeIncrementVo;

import java.util.List;
import java.util.Map;

public interface StudyTimeService {
    /** 返回 module -> { totalMs, lastActiveAt }，包含所有标准模块（缺失的填 0）。 */
    Map<String, Map<String, Object>> getByLesson(Long userId, Integer lessonId);

    JfStudyTime increment(Long userId, Integer lessonId, String module, StudyTimeIncrementVo vo);

    void resetByLesson(Long userId, Integer lessonId);

    List<JfStudyTime> rawListByLesson(Long userId, Integer lessonId);
}
