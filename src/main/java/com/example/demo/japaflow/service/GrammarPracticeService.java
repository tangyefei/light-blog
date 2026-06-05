package com.example.demo.japaflow.service;

import com.example.demo.japaflow.entity.JfGrammarPractice;
import com.example.demo.japaflow.vo.GrammarPracticeUpdateVo;

import java.util.List;

public interface GrammarPracticeService {
    List<JfGrammarPractice> listByLesson(Long userId, Integer lessonId);

    JfGrammarPractice upsert(Long userId, Integer lessonId, String grammarId, Integer exampleIndex, GrammarPracticeUpdateVo vo);

    void resetByLesson(Long userId, Integer lessonId);
}
