package com.example.demo.japaflow.service;

import com.example.demo.japaflow.entity.JfLessonPreference;
import com.example.demo.japaflow.vo.LessonPreferenceUpdateVo;

public interface LessonPreferenceService {
    /** 返回当前偏好；不存在时返回包含默认值的对象（不写入）。 */
    JfLessonPreference get(Long userId, Integer lessonId);

    JfLessonPreference upsert(Long userId, Integer lessonId, LessonPreferenceUpdateVo vo);

    void resetByLesson(Long userId, Integer lessonId);
}
