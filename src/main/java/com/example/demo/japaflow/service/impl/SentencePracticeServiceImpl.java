package com.example.demo.japaflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.japaflow.entity.JfSentencePractice;
import com.example.demo.japaflow.mapper.JfSentencePracticeMapper;
import com.example.demo.japaflow.service.SentencePracticeService;
import com.example.demo.japaflow.vo.SentencePracticeUpdateVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SentencePracticeServiceImpl implements SentencePracticeService {

    private final JfSentencePracticeMapper mapper;

    @Override
    public List<JfSentencePractice> listByLesson(Long userId, Integer lessonId) {
        return mapper.selectList(new LambdaQueryWrapper<JfSentencePractice>()
                .eq(JfSentencePractice::getUserId, userId)
                .eq(JfSentencePractice::getLessonId, lessonId));
    }

    @Override
    public JfSentencePractice upsert(Long userId, Integer lessonId, String sentenceId, SentencePracticeUpdateVo vo) {
        JfSentencePractice existing = mapper.selectOne(new LambdaQueryWrapper<JfSentencePractice>()
                .eq(JfSentencePractice::getUserId, userId)
                .eq(JfSentencePractice::getLessonId, lessonId)
                .eq(JfSentencePractice::getSentenceId, sentenceId));

        boolean isNew = existing == null;
        if (isNew) {
            existing = new JfSentencePractice();
            existing.setUserId(userId);
            existing.setLessonId(lessonId);
            existing.setSentenceId(sentenceId);
            existing.setPronunciationPassed(false);
            existing.setPronunciationScore(0);
            existing.setAccuracyScore(0);
            existing.setFluencyScore(0);
            existing.setCompletenessScore(0);
            existing.setRecognizedText("");
            existing.setPronunciationAttempts(0);
        }

        if (vo.getPronunciationPassed() != null) existing.setPronunciationPassed(vo.getPronunciationPassed());
        if (vo.getPronunciationScore() != null) existing.setPronunciationScore(vo.getPronunciationScore());
        if (vo.getAccuracyScore() != null) existing.setAccuracyScore(vo.getAccuracyScore());
        if (vo.getFluencyScore() != null) existing.setFluencyScore(vo.getFluencyScore());
        if (vo.getCompletenessScore() != null) existing.setCompletenessScore(vo.getCompletenessScore());
        if (vo.getPronunciationReasons() != null) existing.setPronunciationReasons(vo.getPronunciationReasons());
        if (vo.getRecognizedText() != null) existing.setRecognizedText(vo.getRecognizedText());
        if (vo.getPronunciationAttempts() != null) existing.setPronunciationAttempts(vo.getPronunciationAttempts());

        if (isNew) mapper.insert(existing);
        else mapper.updateById(existing);
        return existing;
    }

    @Override
    public void resetByLesson(Long userId, Integer lessonId) {
        mapper.delete(new LambdaQueryWrapper<JfSentencePractice>()
                .eq(JfSentencePractice::getUserId, userId)
                .eq(JfSentencePractice::getLessonId, lessonId));
    }
}
