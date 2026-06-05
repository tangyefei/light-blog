package com.example.demo.japaflow.service;

import com.example.demo.japaflow.entity.JfWordLearning;
import com.example.demo.japaflow.vo.WordLearningResponseVo;
import com.example.demo.japaflow.vo.WordLearningUpdateVo;

import java.util.List;

public interface WordLearningService {
    List<WordLearningResponseVo> listByLesson(Long userId, Integer lessonId);

    WordLearningResponseVo upsert(Long userId, Integer lessonId, String wordId, WordLearningUpdateVo vo);

    void resetByLesson(Long userId, Integer lessonId);

    List<JfWordLearning> findRawByLesson(Long userId, Integer lessonId);
}
