package com.example.demo.japaflow.service;

import com.example.demo.japaflow.entity.JfInteractionProgress;
import com.example.demo.japaflow.vo.InteractionProgressUpdateVo;

import java.util.List;
import java.util.Map;

public interface InteractionProgressService {
    List<JfInteractionProgress> listByLesson(Long userId, Integer lessonId);

    Map<String, Map<String, Map<String, Object>>> listGrouped(Long userId, Integer lessonId);

    JfInteractionProgress upsert(Long userId, Integer lessonId, String itemType, String itemId, InteractionProgressUpdateVo vo);

    void resetByLesson(Long userId, Integer lessonId);
}
