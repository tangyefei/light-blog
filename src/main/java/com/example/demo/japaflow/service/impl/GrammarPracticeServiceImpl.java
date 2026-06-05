package com.example.demo.japaflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.japaflow.entity.JfGrammarPractice;
import com.example.demo.japaflow.mapper.JfGrammarPracticeMapper;
import com.example.demo.japaflow.service.GrammarPracticeService;
import com.example.demo.japaflow.vo.GrammarPracticeUpdateVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GrammarPracticeServiceImpl implements GrammarPracticeService {

    private final JfGrammarPracticeMapper mapper;

    @Override
    public List<JfGrammarPractice> listByLesson(Long userId, Integer lessonId) {
        return mapper.selectList(new LambdaQueryWrapper<JfGrammarPractice>()
                .eq(JfGrammarPractice::getUserId, userId)
                .eq(JfGrammarPractice::getLessonId, lessonId));
    }

    @Override
    public JfGrammarPractice upsert(Long userId, Integer lessonId, String grammarId, Integer exampleIndex, GrammarPracticeUpdateVo vo) {
        JfGrammarPractice existing = mapper.selectOne(new LambdaQueryWrapper<JfGrammarPractice>()
                .eq(JfGrammarPractice::getUserId, userId)
                .eq(JfGrammarPractice::getLessonId, lessonId)
                .eq(JfGrammarPractice::getGrammarId, grammarId)
                .eq(JfGrammarPractice::getExampleIndex, exampleIndex));

        boolean isNew = existing == null;
        if (isNew) {
            existing = new JfGrammarPractice();
            existing.setUserId(userId);
            existing.setLessonId(lessonId);
            existing.setGrammarId(grammarId);
            existing.setExampleIndex(exampleIndex);
            existing.setSubmitted(false);
            existing.setCorrect(false);
            existing.setRevealed(false);
            existing.setAttempts(0);
            existing.setPronunciationPassed(false);
            existing.setPronunciationScore(0);
            existing.setAccuracyScore(0);
            existing.setFluencyScore(0);
            existing.setCompletenessScore(0);
            existing.setRecognizedText("");
            existing.setPronunciationAttempts(0);
        }

        if (vo.getAnswer() != null) existing.setAnswer(vo.getAnswer());
        if (vo.getSubmitted() != null) existing.setSubmitted(vo.getSubmitted());
        if (vo.getCorrect() != null) existing.setCorrect(vo.getCorrect());
        if (vo.getRevealed() != null) existing.setRevealed(vo.getRevealed());
        if (vo.getAttempts() != null) existing.setAttempts(vo.getAttempts());
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
        mapper.delete(new LambdaQueryWrapper<JfGrammarPractice>()
                .eq(JfGrammarPractice::getUserId, userId)
                .eq(JfGrammarPractice::getLessonId, lessonId));
    }
}
