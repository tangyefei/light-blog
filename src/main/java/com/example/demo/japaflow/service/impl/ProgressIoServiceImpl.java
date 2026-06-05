package com.example.demo.japaflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.common.BusinessException;
import com.example.demo.common.ResultCode;
import com.example.demo.japaflow.entity.*;
import com.example.demo.japaflow.mapper.*;
import com.example.demo.japaflow.service.ProgressIoService;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ProgressIoServiceImpl implements ProgressIoService {

    private final JfWordLearningMapper wordMapper;
    private final JfGrammarPracticeMapper grammarMapper;
    private final JfSentencePracticeMapper sentenceMapper;
    private final JfExerciseResultMapper exerciseMapper;
    private final JfWrongBookMapper wrongBookMapper;
    private final JfInteractionProgressMapper interactionMapper;
    private final JfStudyTimeMapper studyTimeMapper;
    private final JfFavoriteMapper favoriteMapper;
    private final JfLessonPreferenceMapper preferenceMapper;
    private final ObjectMapper objectMapper;

    @Override
    public Map<String, Object> exportAll(Long userId) {
        Set<Integer> lessons = new TreeSet<>();
        wordMapper.selectList(new LambdaQueryWrapper<JfWordLearning>().eq(JfWordLearning::getUserId, userId))
                .forEach(e -> lessons.add(e.getLessonId()));
        grammarMapper.selectList(new LambdaQueryWrapper<JfGrammarPractice>().eq(JfGrammarPractice::getUserId, userId))
                .forEach(e -> lessons.add(e.getLessonId()));
        sentenceMapper.selectList(new LambdaQueryWrapper<JfSentencePractice>().eq(JfSentencePractice::getUserId, userId))
                .forEach(e -> lessons.add(e.getLessonId()));
        exerciseMapper.selectList(new LambdaQueryWrapper<JfExerciseResult>().eq(JfExerciseResult::getUserId, userId))
                .forEach(e -> lessons.add(e.getLessonId()));
        wrongBookMapper.selectList(new LambdaQueryWrapper<JfWrongBook>().eq(JfWrongBook::getUserId, userId))
                .forEach(e -> lessons.add(e.getLessonId()));
        interactionMapper.selectList(new LambdaQueryWrapper<JfInteractionProgress>().eq(JfInteractionProgress::getUserId, userId))
                .forEach(e -> lessons.add(e.getLessonId()));
        studyTimeMapper.selectList(new LambdaQueryWrapper<JfStudyTime>().eq(JfStudyTime::getUserId, userId))
                .forEach(e -> lessons.add(e.getLessonId()));
        favoriteMapper.selectList(new LambdaQueryWrapper<JfFavorite>().eq(JfFavorite::getUserId, userId))
                .forEach(e -> lessons.add(e.getLessonId()));
        preferenceMapper.selectList(new LambdaQueryWrapper<JfLessonPreference>().eq(JfLessonPreference::getUserId, userId))
                .forEach(e -> lessons.add(e.getLessonId()));

        Map<String, Object> lessonsMap = new LinkedHashMap<>();
        for (Integer lessonId : lessons) {
            lessonsMap.put(String.valueOf(lessonId), buildLesson(userId, lessonId));
        }

        Map<String, Object> root = new LinkedHashMap<>();
        root.put("exportedAt", Instant.now().toString());
        root.put("lessons", lessonsMap);
        return root;
    }

    private Map<String, Object> buildLesson(Long userId, Integer lessonId) {
        Map<String, Object> lesson = new LinkedHashMap<>();

        Map<String, Object> wordLearning = new LinkedHashMap<>();
        for (JfWordLearning w : wordMapper.selectList(new LambdaQueryWrapper<JfWordLearning>()
                .eq(JfWordLearning::getUserId, userId).eq(JfWordLearning::getLessonId, lessonId))) {
            wordLearning.put(w.getWordId(), w);
        }
        lesson.put("wordLearning", wordLearning);

        Map<String, Object> grammar = new LinkedHashMap<>();
        for (JfGrammarPractice g : grammarMapper.selectList(new LambdaQueryWrapper<JfGrammarPractice>()
                .eq(JfGrammarPractice::getUserId, userId).eq(JfGrammarPractice::getLessonId, lessonId))) {
            grammar.put(g.getGrammarId() + "_" + g.getExampleIndex(), g);
        }
        lesson.put("grammarPractice", grammar);

        Map<String, Object> sentences = new LinkedHashMap<>();
        for (JfSentencePractice s : sentenceMapper.selectList(new LambdaQueryWrapper<JfSentencePractice>()
                .eq(JfSentencePractice::getUserId, userId).eq(JfSentencePractice::getLessonId, lessonId))) {
            sentences.put(s.getSentenceId(), s);
        }
        lesson.put("sentencePractice", sentences);

        lesson.put("exerciseResults", exerciseMapper.selectList(new LambdaQueryWrapper<JfExerciseResult>()
                .eq(JfExerciseResult::getUserId, userId).eq(JfExerciseResult::getLessonId, lessonId)));

        lesson.put("wrongBook", wrongBookMapper.selectList(new LambdaQueryWrapper<JfWrongBook>()
                .eq(JfWrongBook::getUserId, userId).eq(JfWrongBook::getLessonId, lessonId)));

        Map<String, Map<String, Object>> interaction = new LinkedHashMap<>();
        interaction.put("words", new LinkedHashMap<>());
        interaction.put("sentences", new LinkedHashMap<>());
        interaction.put("grammarExamples", new LinkedHashMap<>());
        for (JfInteractionProgress p : interactionMapper.selectList(new LambdaQueryWrapper<JfInteractionProgress>()
                .eq(JfInteractionProgress::getUserId, userId).eq(JfInteractionProgress::getLessonId, lessonId))) {
            String key = "word".equals(p.getItemType()) ? "words"
                    : "sentence".equals(p.getItemType()) ? "sentences"
                    : "grammarExample".equals(p.getItemType()) ? "grammarExamples" : p.getItemType();
            interaction.computeIfAbsent(key, k -> new LinkedHashMap<>()).put(p.getItemId(), p);
        }
        lesson.put("interactionProgress", interaction);

        Map<String, Object> studyTime = new LinkedHashMap<>();
        for (JfStudyTime t : studyTimeMapper.selectList(new LambdaQueryWrapper<JfStudyTime>()
                .eq(JfStudyTime::getUserId, userId).eq(JfStudyTime::getLessonId, lessonId))) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("totalMs", t.getTotalMs());
            m.put("lastActiveAt", t.getLastActiveAt());
            studyTime.put(t.getModule(), m);
        }
        lesson.put("studyTime", studyTime);

        Map<String, Map<String, Object>> favorites = new LinkedHashMap<>();
        favorites.put("words", new LinkedHashMap<>());
        favorites.put("sentences", new LinkedHashMap<>());
        for (JfFavorite f : favoriteMapper.selectList(new LambdaQueryWrapper<JfFavorite>()
                .eq(JfFavorite::getUserId, userId).eq(JfFavorite::getLessonId, lessonId))) {
            String key = "word".equals(f.getItemType()) ? "words"
                    : "sentence".equals(f.getItemType()) ? "sentences" : f.getItemType();
            favorites.computeIfAbsent(key, k -> new LinkedHashMap<>()).put(f.getItemId(), f);
        }
        lesson.put("favorites", favorites);

        JfLessonPreference pref = preferenceMapper.selectOne(new LambdaQueryWrapper<JfLessonPreference>()
                .eq(JfLessonPreference::getUserId, userId).eq(JfLessonPreference::getLessonId, lessonId));
        lesson.put("preferences", pref);

        return lesson;
    }

    @Override
    @Transactional
    public int importAll(Long userId, Map<String, Object> payload) {
        if (payload == null) throw new BusinessException(ResultCode.BAD_REQUEST, "请求体不能为空");
        Object lessonsObj = payload.get("lessons");
        if (!(lessonsObj instanceof Map)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "lessons 字段缺失或格式错误");
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> lessons = (Map<String, Object>) lessonsObj;

        int count = 0;
        for (Map.Entry<String, Object> entry : lessons.entrySet()) {
            Integer lessonId;
            try {
                lessonId = Integer.parseInt(entry.getKey());
            } catch (NumberFormatException ex) {
                continue;
            }
            if (!(entry.getValue() instanceof Map)) continue;
            @SuppressWarnings("unchecked")
            Map<String, Object> lesson = (Map<String, Object>) entry.getValue();

            // 全量覆盖：先删除该课所有旧数据
            wordMapper.delete(new LambdaQueryWrapper<JfWordLearning>().eq(JfWordLearning::getUserId, userId).eq(JfWordLearning::getLessonId, lessonId));
            grammarMapper.delete(new LambdaQueryWrapper<JfGrammarPractice>().eq(JfGrammarPractice::getUserId, userId).eq(JfGrammarPractice::getLessonId, lessonId));
            sentenceMapper.delete(new LambdaQueryWrapper<JfSentencePractice>().eq(JfSentencePractice::getUserId, userId).eq(JfSentencePractice::getLessonId, lessonId));
            exerciseMapper.delete(new LambdaQueryWrapper<JfExerciseResult>().eq(JfExerciseResult::getUserId, userId).eq(JfExerciseResult::getLessonId, lessonId));
            wrongBookMapper.delete(new LambdaQueryWrapper<JfWrongBook>().eq(JfWrongBook::getUserId, userId).eq(JfWrongBook::getLessonId, lessonId));
            interactionMapper.delete(new LambdaQueryWrapper<JfInteractionProgress>().eq(JfInteractionProgress::getUserId, userId).eq(JfInteractionProgress::getLessonId, lessonId));
            studyTimeMapper.delete(new LambdaQueryWrapper<JfStudyTime>().eq(JfStudyTime::getUserId, userId).eq(JfStudyTime::getLessonId, lessonId));
            favoriteMapper.delete(new LambdaQueryWrapper<JfFavorite>().eq(JfFavorite::getUserId, userId).eq(JfFavorite::getLessonId, lessonId));
            preferenceMapper.delete(new LambdaQueryWrapper<JfLessonPreference>().eq(JfLessonPreference::getUserId, userId).eq(JfLessonPreference::getLessonId, lessonId));

            insertWordLearning(userId, lessonId, lesson.get("wordLearning"));
            insertGrammar(userId, lessonId, lesson.get("grammarPractice"));
            insertSentence(userId, lessonId, lesson.get("sentencePractice"));
            insertExercises(userId, lessonId, lesson.get("exerciseResults"));
            insertWrongBook(userId, lessonId, lesson.get("wrongBook"));
            insertInteraction(userId, lessonId, lesson.get("interactionProgress"));
            insertStudyTime(userId, lessonId, lesson.get("studyTime"));
            insertFavorites(userId, lessonId, lesson.get("favorites"));
            insertPreferences(userId, lessonId, lesson.get("preferences"));

            count++;
        }
        return count;
    }

    private void insertWordLearning(Long userId, Integer lessonId, Object data) {
        if (!(data instanceof Map)) return;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) data;
        for (Map.Entry<String, Object> e : map.entrySet()) {
            if (!(e.getValue() instanceof Map)) continue;
            JfWordLearning w = objectMapper.convertValue(e.getValue(), JfWordLearning.class);
            w.setId(null);
            w.setUserId(userId);
            w.setLessonId(lessonId);
            w.setWordId(e.getKey());
            wordMapper.insert(w);
        }
    }

    private void insertGrammar(Long userId, Integer lessonId, Object data) {
        if (!(data instanceof Map)) return;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) data;
        for (Object value : map.values()) {
            if (!(value instanceof Map)) continue;
            JfGrammarPractice g = objectMapper.convertValue(value, JfGrammarPractice.class);
            g.setId(null);
            g.setUserId(userId);
            g.setLessonId(lessonId);
            if (g.getGrammarId() == null || g.getExampleIndex() == null) continue;
            grammarMapper.insert(g);
        }
    }

    private void insertSentence(Long userId, Integer lessonId, Object data) {
        if (!(data instanceof Map)) return;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) data;
        for (Map.Entry<String, Object> e : map.entrySet()) {
            if (!(e.getValue() instanceof Map)) continue;
            JfSentencePractice s = objectMapper.convertValue(e.getValue(), JfSentencePractice.class);
            s.setId(null);
            s.setUserId(userId);
            s.setLessonId(lessonId);
            s.setSentenceId(e.getKey());
            sentenceMapper.insert(s);
        }
    }

    private void insertExercises(Long userId, Integer lessonId, Object data) {
        if (!(data instanceof List)) return;
        for (Object item : (List<?>) data) {
            if (!(item instanceof Map)) continue;
            JfExerciseResult r = objectMapper.convertValue(item, JfExerciseResult.class);
            r.setId(null);
            r.setUserId(userId);
            r.setLessonId(lessonId);
            if (r.getExerciseId() == null) continue;
            exerciseMapper.insert(r);
        }
    }

    private void insertWrongBook(Long userId, Integer lessonId, Object data) {
        if (!(data instanceof List)) return;
        for (Object item : (List<?>) data) {
            if (!(item instanceof Map)) continue;
            JfWrongBook w = objectMapper.convertValue(item, JfWrongBook.class);
            w.setId(null);
            w.setUserId(userId);
            w.setLessonId(lessonId);
            if (w.getItemType() == null || w.getItemId() == null) continue;
            wrongBookMapper.insert(w);
        }
    }

    private void insertInteraction(Long userId, Integer lessonId, Object data) {
        if (!(data instanceof Map)) return;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) data;
        for (Map.Entry<String, Object> outer : map.entrySet()) {
            String groupKey = outer.getKey();
            String itemType = "words".equals(groupKey) ? "word"
                    : "sentences".equals(groupKey) ? "sentence"
                    : "grammarExamples".equals(groupKey) ? "grammarExample" : groupKey;
            if (!(outer.getValue() instanceof Map)) continue;
            @SuppressWarnings("unchecked")
            Map<String, Object> bucket = (Map<String, Object>) outer.getValue();
            for (Map.Entry<String, Object> inner : bucket.entrySet()) {
                if (!(inner.getValue() instanceof Map)) continue;
                JfInteractionProgress p = objectMapper.convertValue(inner.getValue(), JfInteractionProgress.class);
                p.setId(null);
                p.setUserId(userId);
                p.setLessonId(lessonId);
                p.setItemType(itemType);
                p.setItemId(inner.getKey());
                interactionMapper.insert(p);
            }
        }
    }

    private void insertStudyTime(Long userId, Integer lessonId, Object data) {
        if (!(data instanceof Map)) return;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) data;
        for (Map.Entry<String, Object> e : map.entrySet()) {
            if (!(e.getValue() instanceof Map)) continue;
            JfStudyTime t = objectMapper.convertValue(e.getValue(), JfStudyTime.class);
            t.setId(null);
            t.setUserId(userId);
            t.setLessonId(lessonId);
            t.setModule(e.getKey());
            studyTimeMapper.insert(t);
        }
    }

    private void insertFavorites(Long userId, Integer lessonId, Object data) {
        if (!(data instanceof Map)) return;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) data;
        for (Map.Entry<String, Object> outer : map.entrySet()) {
            String groupKey = outer.getKey();
            String itemType = "words".equals(groupKey) ? "word"
                    : "sentences".equals(groupKey) ? "sentence" : groupKey;
            if (!(outer.getValue() instanceof Map)) continue;
            @SuppressWarnings("unchecked")
            Map<String, Object> bucket = (Map<String, Object>) outer.getValue();
            for (Map.Entry<String, Object> inner : bucket.entrySet()) {
                if (!(inner.getValue() instanceof Map)) continue;
                JfFavorite f = objectMapper.convertValue(inner.getValue(), JfFavorite.class);
                f.setId(null);
                f.setUserId(userId);
                f.setLessonId(lessonId);
                f.setItemType(itemType);
                f.setItemId(inner.getKey());
                favoriteMapper.insert(f);
            }
        }
    }

    private void insertPreferences(Long userId, Integer lessonId, Object data) {
        if (!(data instanceof Map)) return;
        JfLessonPreference p = objectMapper.convertValue(data, JfLessonPreference.class);
        p.setId(null);
        p.setUserId(userId);
        p.setLessonId(lessonId);
        preferenceMapper.insert(p);
    }
}
