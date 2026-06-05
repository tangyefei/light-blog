package com.example.demo.japaflow.service;

import com.example.demo.japaflow.entity.JfExerciseResult;
import com.example.demo.japaflow.vo.ExerciseSubmitVo;

import java.util.List;

public interface ExerciseService {
    List<JfExerciseResult> listByLesson(Long userId, Integer lessonId);

    JfExerciseResult submit(Long userId, Integer lessonId, String exerciseId, ExerciseSubmitVo vo);

    void resetByLesson(Long userId, Integer lessonId);
}
