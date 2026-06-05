package com.example.demo.japaflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.japaflow.entity.*;
import com.example.demo.japaflow.mapper.*;
import com.example.demo.japaflow.service.ProgressSummaryService;
import com.example.demo.japaflow.vo.ProgressSummaryVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressSummaryServiceImpl implements ProgressSummaryService {

    private final JfWordLearningMapper wordMapper;
    private final JfGrammarPracticeMapper grammarMapper;
    private final JfSentencePracticeMapper sentenceMapper;
    private final JfExerciseResultMapper exerciseMapper;
    private final JfWrongBookMapper wrongBookMapper;
    private final JfStudyTimeMapper studyTimeMapper;

    @Override
    public List<ProgressSummaryVo> summary(Long userId, List<Integer> lessonIds) {
        Set<Integer> lessons = new TreeSet<>();
        if (lessonIds != null && !lessonIds.isEmpty()) {
            lessons.addAll(lessonIds);
        } else {
            wordMapper.selectList(new LambdaQueryWrapper<JfWordLearning>().select(JfWordLearning::getLessonId).eq(JfWordLearning::getUserId, userId))
                    .forEach(w -> lessons.add(w.getLessonId()));
            grammarMapper.selectList(new LambdaQueryWrapper<JfGrammarPractice>().select(JfGrammarPractice::getLessonId).eq(JfGrammarPractice::getUserId, userId))
                    .forEach(g -> lessons.add(g.getLessonId()));
            sentenceMapper.selectList(new LambdaQueryWrapper<JfSentencePractice>().select(JfSentencePractice::getLessonId).eq(JfSentencePractice::getUserId, userId))
                    .forEach(s -> lessons.add(s.getLessonId()));
            exerciseMapper.selectList(new LambdaQueryWrapper<JfExerciseResult>().select(JfExerciseResult::getLessonId).eq(JfExerciseResult::getUserId, userId))
                    .forEach(e -> lessons.add(e.getLessonId()));
            studyTimeMapper.selectList(new LambdaQueryWrapper<JfStudyTime>().select(JfStudyTime::getLessonId).eq(JfStudyTime::getUserId, userId))
                    .forEach(t -> lessons.add(t.getLessonId()));
        }

        List<ProgressSummaryVo> out = new ArrayList<>();
        for (Integer lessonId : lessons) {
            out.add(buildOne(userId, lessonId));
        }
        return out;
    }

    private ProgressSummaryVo buildOne(Long userId, Integer lessonId) {
        ProgressSummaryVo vo = new ProgressSummaryVo();
        vo.setLessonId(lessonId);

        List<JfWordLearning> words = wordMapper.selectList(new LambdaQueryWrapper<JfWordLearning>()
                .eq(JfWordLearning::getUserId, userId).eq(JfWordLearning::getLessonId, lessonId));
        int wordsDone = (int) words.stream().filter(w -> "mastered".equals(w.getMainStatus()) || Boolean.TRUE.equals(w.getSlashed())).count();
        vo.setVocab(new ProgressSummaryVo.CountVo(wordsDone, null));

        List<JfGrammarPractice> grammars = grammarMapper.selectList(new LambdaQueryWrapper<JfGrammarPractice>()
                .eq(JfGrammarPractice::getUserId, userId).eq(JfGrammarPractice::getLessonId, lessonId));
        Set<String> grammarPassed = grammars.stream()
                .filter(g -> Boolean.TRUE.equals(g.getCorrect()) || Boolean.TRUE.equals(g.getPronunciationPassed()))
                .map(JfGrammarPractice::getGrammarId)
                .collect(Collectors.toSet());
        vo.setGrammar(new ProgressSummaryVo.CountVo(grammarPassed.size(), null));

        List<JfSentencePractice> sentences = sentenceMapper.selectList(new LambdaQueryWrapper<JfSentencePractice>()
                .eq(JfSentencePractice::getUserId, userId).eq(JfSentencePractice::getLessonId, lessonId));
        int sentenceDone = (int) sentences.stream().filter(s -> Boolean.TRUE.equals(s.getPronunciationPassed())).count();
        vo.setText(new ProgressSummaryVo.CountVo(sentenceDone, null));

        List<JfExerciseResult> exercises = exerciseMapper.selectList(new LambdaQueryWrapper<JfExerciseResult>()
                .eq(JfExerciseResult::getUserId, userId).eq(JfExerciseResult::getLessonId, lessonId));
        int exDone = (int) exercises.stream().filter(e -> Boolean.TRUE.equals(e.getCorrect())).count();
        vo.setExercises(new ProgressSummaryVo.CountVo(exDone, null));

        Long weakCount = wrongBookMapper.selectCount(new LambdaQueryWrapper<JfWrongBook>()
                .eq(JfWrongBook::getUserId, userId)
                .eq(JfWrongBook::getLessonId, lessonId)
                .eq(JfWrongBook::getResolved, false));
        vo.setWeak(new ProgressSummaryVo.CountVo(weakCount.intValue(), null));

        long totalMs = studyTimeMapper.selectList(new LambdaQueryWrapper<JfStudyTime>()
                        .eq(JfStudyTime::getUserId, userId).eq(JfStudyTime::getLessonId, lessonId))
                .stream().mapToLong(t -> t.getTotalMs() != null ? t.getTotalMs() : 0L).sum();
        vo.setTotalStudyTimeMs(totalMs);

        boolean any = !words.isEmpty() || !grammars.isEmpty() || !sentences.isEmpty() || !exercises.isEmpty() || totalMs > 0;
        vo.setStatus(any ? "learning" : "untouched");
        vo.setPercent(null);

        return vo;
    }
}
