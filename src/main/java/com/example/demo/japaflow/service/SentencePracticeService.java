package com.example.demo.japaflow.service;

import com.example.demo.japaflow.entity.JfSentencePractice;
import com.example.demo.japaflow.vo.SentencePracticeUpdateVo;

import java.util.List;

public interface SentencePracticeService {
    List<JfSentencePractice> listByLesson(Long userId, Integer lessonId);

    JfSentencePractice upsert(Long userId, Integer lessonId, String sentenceId, SentencePracticeUpdateVo vo);

    void resetByLesson(Long userId, Integer lessonId);
}
