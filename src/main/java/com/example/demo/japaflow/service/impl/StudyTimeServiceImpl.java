package com.example.demo.japaflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.japaflow.entity.JfStudyTime;
import com.example.demo.japaflow.mapper.JfStudyTimeMapper;
import com.example.demo.japaflow.service.StudyTimeService;
import com.example.demo.japaflow.vo.StudyTimeIncrementVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StudyTimeServiceImpl implements StudyTimeService {

    public static final List<String> MODULES = Arrays.asList(
            "vocab", "grammar", "text", "exercises", "wrongbook", "favorites");

    private final JfStudyTimeMapper mapper;

    @Override
    public List<JfStudyTime> rawListByLesson(Long userId, Integer lessonId) {
        return mapper.selectList(new LambdaQueryWrapper<JfStudyTime>()
                .eq(JfStudyTime::getUserId, userId)
                .eq(JfStudyTime::getLessonId, lessonId));
    }

    @Override
    public Map<String, Map<String, Object>> getByLesson(Long userId, Integer lessonId) {
        Map<String, Map<String, Object>> result = new LinkedHashMap<>();
        for (String m : MODULES) {
            Map<String, Object> empty = new HashMap<>();
            empty.put("totalMs", 0L);
            empty.put("lastActiveAt", null);
            result.put(m, empty);
        }
        for (JfStudyTime t : rawListByLesson(userId, lessonId)) {
            Map<String, Object> map = new HashMap<>();
            map.put("totalMs", t.getTotalMs() != null ? t.getTotalMs() : 0L);
            map.put("lastActiveAt", t.getLastActiveAt());
            result.put(t.getModule(), map);
        }
        return result;
    }

    @Override
    @Transactional
    public JfStudyTime increment(Long userId, Integer lessonId, String module, StudyTimeIncrementVo vo) {
        long delta = vo.getDeltaMs() != null ? vo.getDeltaMs() : 0L;
        LocalDateTime activeAt = vo.getActiveAt() != null ? vo.getActiveAt() : LocalDateTime.now();

        JfStudyTime existing = mapper.selectOne(new LambdaQueryWrapper<JfStudyTime>()
                .eq(JfStudyTime::getUserId, userId)
                .eq(JfStudyTime::getLessonId, lessonId)
                .eq(JfStudyTime::getModule, module));

        if (existing == null) {
            JfStudyTime e = new JfStudyTime();
            e.setUserId(userId);
            e.setLessonId(lessonId);
            e.setModule(module);
            e.setTotalMs(Math.max(delta, 0L));
            e.setLastActiveAt(activeAt);
            mapper.insert(e);
            return e;
        }
        mapper.incrementTotalMs(userId, lessonId, module, delta, activeAt);
        return mapper.selectById(existing.getId());
    }

    @Override
    public void resetByLesson(Long userId, Integer lessonId) {
        mapper.delete(new LambdaQueryWrapper<JfStudyTime>()
                .eq(JfStudyTime::getUserId, userId)
                .eq(JfStudyTime::getLessonId, lessonId));
    }
}
