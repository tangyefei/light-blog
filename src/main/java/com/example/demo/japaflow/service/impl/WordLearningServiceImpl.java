package com.example.demo.japaflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.japaflow.entity.JfWordLearning;
import com.example.demo.japaflow.mapper.JfWordLearningMapper;
import com.example.demo.japaflow.service.WordLearningService;
import com.example.demo.japaflow.vo.WordLearningResponseVo;
import com.example.demo.japaflow.vo.WordLearningUpdateVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WordLearningServiceImpl implements WordLearningService {

    private final JfWordLearningMapper mapper;

    @Override
    public List<WordLearningResponseVo> listByLesson(Long userId, Integer lessonId) {
        return findRawByLesson(userId, lessonId).stream()
                .map(this::toVo)
                .collect(Collectors.toList());
    }

    @Override
    public List<JfWordLearning> findRawByLesson(Long userId, Integer lessonId) {
        return mapper.selectList(new LambdaQueryWrapper<JfWordLearning>()
                .eq(JfWordLearning::getUserId, userId)
                .eq(JfWordLearning::getLessonId, lessonId));
    }

    @Override
    public WordLearningResponseVo upsert(Long userId, Integer lessonId, String wordId, WordLearningUpdateVo vo) {
        JfWordLearning existing = mapper.selectOne(new LambdaQueryWrapper<JfWordLearning>()
                .eq(JfWordLearning::getUserId, userId)
                .eq(JfWordLearning::getLessonId, lessonId)
                .eq(JfWordLearning::getWordId, wordId));

        boolean isNew = existing == null;
        if (isNew) {
            existing = new JfWordLearning();
            existing.setUserId(userId);
            existing.setLessonId(lessonId);
            existing.setWordId(wordId);
            existing.setMainStatus("new");
            existing.setSlashed(false);
            existing.setMeaningToWordCorrect(false);
            existing.setAudioToWordCorrect(false);
            existing.setWordToMeaningCorrect(false);
            existing.setPronunciationPassed(false);
            existing.setPronunciationScore(0);
            existing.setAccuracyScore(0);
            existing.setFluencyScore(0);
            existing.setCompletenessScore(0);
            existing.setRecognizedText("");
            existing.setAttemptsMeaningToWord(0);
            existing.setAttemptsAudioToWord(0);
            existing.setAttemptsWordToMeaning(0);
            existing.setAttemptsPronunciation(0);
        }

        if (vo.getMainStatus() != null) existing.setMainStatus(vo.getMainStatus());
        if (vo.getSlashed() != null) existing.setSlashed(vo.getSlashed());
        if (vo.getMeaningToWordCorrect() != null) existing.setMeaningToWordCorrect(vo.getMeaningToWordCorrect());
        if (vo.getAudioToWordCorrect() != null) existing.setAudioToWordCorrect(vo.getAudioToWordCorrect());
        if (vo.getWordToMeaningCorrect() != null) existing.setWordToMeaningCorrect(vo.getWordToMeaningCorrect());
        if (vo.getPronunciationPassed() != null) existing.setPronunciationPassed(vo.getPronunciationPassed());
        if (vo.getPronunciationScore() != null) existing.setPronunciationScore(vo.getPronunciationScore());
        if (vo.getAccuracyScore() != null) existing.setAccuracyScore(vo.getAccuracyScore());
        if (vo.getFluencyScore() != null) existing.setFluencyScore(vo.getFluencyScore());
        if (vo.getCompletenessScore() != null) existing.setCompletenessScore(vo.getCompletenessScore());
        if (vo.getDiagnosticTags() != null) existing.setDiagnosticTags(vo.getDiagnosticTags());
        if (vo.getPronunciationReasons() != null) existing.setPronunciationReasons(vo.getPronunciationReasons());
        if (vo.getRecognizedText() != null) existing.setRecognizedText(vo.getRecognizedText());

        if (vo.getAttempts() != null) {
            WordLearningUpdateVo.AttemptsVo a = vo.getAttempts();
            if (a.getMeaningToWord() != null) existing.setAttemptsMeaningToWord(a.getMeaningToWord());
            if (a.getAudioToWord() != null) existing.setAttemptsAudioToWord(a.getAudioToWord());
            if (a.getWordToMeaning() != null) existing.setAttemptsWordToMeaning(a.getWordToMeaning());
            if (a.getPronunciation() != null) existing.setAttemptsPronunciation(a.getPronunciation());
        }

        existing.setLastPracticedAt(LocalDateTime.now());

        if (isNew) {
            mapper.insert(existing);
        } else {
            mapper.updateById(existing);
        }
        return toVo(existing);
    }

    @Override
    public void resetByLesson(Long userId, Integer lessonId) {
        mapper.delete(new LambdaQueryWrapper<JfWordLearning>()
                .eq(JfWordLearning::getUserId, userId)
                .eq(JfWordLearning::getLessonId, lessonId));
    }

    private WordLearningResponseVo toVo(JfWordLearning e) {
        WordLearningResponseVo v = new WordLearningResponseVo();
        v.setWordId(e.getWordId());
        v.setMainStatus(e.getMainStatus());
        v.setSlashed(e.getSlashed());
        v.setMeaningToWordCorrect(e.getMeaningToWordCorrect());
        v.setAudioToWordCorrect(e.getAudioToWordCorrect());
        v.setWordToMeaningCorrect(e.getWordToMeaningCorrect());
        v.setPronunciationPassed(e.getPronunciationPassed());
        v.setPronunciationScore(e.getPronunciationScore());
        v.setAccuracyScore(e.getAccuracyScore());
        v.setFluencyScore(e.getFluencyScore());
        v.setCompletenessScore(e.getCompletenessScore());
        v.setDiagnosticTags(e.getDiagnosticTags());
        v.setPronunciationReasons(e.getPronunciationReasons());
        v.setRecognizedText(e.getRecognizedText());

        WordLearningResponseVo.AttemptsVo a = new WordLearningResponseVo.AttemptsVo();
        a.setMeaningToWord(e.getAttemptsMeaningToWord());
        a.setAudioToWord(e.getAttemptsAudioToWord());
        a.setWordToMeaning(e.getAttemptsWordToMeaning());
        a.setPronunciation(e.getAttemptsPronunciation());
        v.setAttempts(a);

        v.setLastPracticedAt(e.getLastPracticedAt());
        return v;
    }
}
