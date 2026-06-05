package com.example.demo.japaflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.japaflow.entity.JfLessonPreference;
import com.example.demo.japaflow.mapper.JfLessonPreferenceMapper;
import com.example.demo.japaflow.service.LessonPreferenceService;
import com.example.demo.japaflow.vo.LessonPreferenceUpdateVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class LessonPreferenceServiceImpl implements LessonPreferenceService {

    private final JfLessonPreferenceMapper mapper;

    @Override
    public JfLessonPreference get(Long userId, Integer lessonId) {
        JfLessonPreference existing = mapper.selectOne(new LambdaQueryWrapper<JfLessonPreference>()
                .eq(JfLessonPreference::getUserId, userId)
                .eq(JfLessonPreference::getLessonId, lessonId));
        if (existing != null) return existing;

        JfLessonPreference defaults = new JfLessonPreference();
        defaults.setUserId(userId);
        defaults.setLessonId(lessonId);
        defaults.setCurrentVoiceId("");
        defaults.setPlaybackRate(new BigDecimal("1.00"));
        defaults.setVocabFocusOnly(false);
        defaults.setCurrentExerciseGroup(0);
        defaults.setTextCurrentTab("basic");
        return defaults;
    }

    @Override
    public JfLessonPreference upsert(Long userId, Integer lessonId, LessonPreferenceUpdateVo vo) {
        JfLessonPreference existing = mapper.selectOne(new LambdaQueryWrapper<JfLessonPreference>()
                .eq(JfLessonPreference::getUserId, userId)
                .eq(JfLessonPreference::getLessonId, lessonId));

        boolean isNew = existing == null;
        if (isNew) {
            existing = new JfLessonPreference();
            existing.setUserId(userId);
            existing.setLessonId(lessonId);
            existing.setCurrentVoiceId("");
            existing.setPlaybackRate(new BigDecimal("1.00"));
            existing.setVocabFocusOnly(false);
            existing.setCurrentExerciseGroup(0);
            existing.setTextCurrentTab("basic");
        }
        if (vo.getCurrentVoiceId() != null) existing.setCurrentVoiceId(vo.getCurrentVoiceId());
        if (vo.getPlaybackRate() != null) existing.setPlaybackRate(vo.getPlaybackRate());
        if (vo.getVocabFocusOnly() != null) existing.setVocabFocusOnly(vo.getVocabFocusOnly());
        if (vo.getCurrentExerciseGroup() != null) existing.setCurrentExerciseGroup(vo.getCurrentExerciseGroup());
        if (vo.getTextCurrentTab() != null) existing.setTextCurrentTab(vo.getTextCurrentTab());

        if (isNew) mapper.insert(existing);
        else mapper.updateById(existing);
        return existing;
    }

    @Override
    public void resetByLesson(Long userId, Integer lessonId) {
        mapper.delete(new LambdaQueryWrapper<JfLessonPreference>()
                .eq(JfLessonPreference::getUserId, userId)
                .eq(JfLessonPreference::getLessonId, lessonId));
    }
}
