package com.example.demo.japaflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.japaflow.entity.JfExerciseResult;
import com.example.demo.japaflow.mapper.JfExerciseResultMapper;
import com.example.demo.japaflow.service.ExerciseService;
import com.example.demo.japaflow.vo.ExerciseSubmitVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExerciseServiceImpl implements ExerciseService {

    private final JfExerciseResultMapper mapper;

    @Override
    public List<JfExerciseResult> listByLesson(Long userId, Integer lessonId) {
        return mapper.selectList(new LambdaQueryWrapper<JfExerciseResult>()
                .eq(JfExerciseResult::getUserId, userId)
                .eq(JfExerciseResult::getLessonId, lessonId));
    }

    @Override
    public JfExerciseResult submit(Long userId, Integer lessonId, String exerciseId, ExerciseSubmitVo vo) {
        JfExerciseResult existing = mapper.selectOne(new LambdaQueryWrapper<JfExerciseResult>()
                .eq(JfExerciseResult::getUserId, userId)
                .eq(JfExerciseResult::getLessonId, lessonId)
                .eq(JfExerciseResult::getExerciseId, exerciseId));

        boolean isNew = existing == null;
        if (isNew) {
            existing = new JfExerciseResult();
            existing.setUserId(userId);
            existing.setLessonId(lessonId);
            existing.setExerciseId(exerciseId);
        }

        existing.setGroupIndex(vo.getGroupIndex() != null ? vo.getGroupIndex() : (existing.getGroupIndex() != null ? existing.getGroupIndex() : 0));
        existing.setAnswer(vo.getAnswer());
        existing.setCorrect(vo.getCorrect() != null ? vo.getCorrect() : false);
        existing.setSubmittedAt(LocalDateTime.now());

        if (isNew) mapper.insert(existing);
        else mapper.updateById(existing);
        return existing;
    }

    @Override
    public void resetByLesson(Long userId, Integer lessonId) {
        mapper.delete(new LambdaQueryWrapper<JfExerciseResult>()
                .eq(JfExerciseResult::getUserId, userId)
                .eq(JfExerciseResult::getLessonId, lessonId));
    }
}
