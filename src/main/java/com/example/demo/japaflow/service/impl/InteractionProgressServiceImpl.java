package com.example.demo.japaflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.japaflow.entity.JfInteractionProgress;
import com.example.demo.japaflow.mapper.JfInteractionProgressMapper;
import com.example.demo.japaflow.service.InteractionProgressService;
import com.example.demo.japaflow.vo.InteractionProgressUpdateVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InteractionProgressServiceImpl implements InteractionProgressService {

    private final JfInteractionProgressMapper mapper;

    @Override
    public List<JfInteractionProgress> listByLesson(Long userId, Integer lessonId) {
        return mapper.selectList(new LambdaQueryWrapper<JfInteractionProgress>()
                .eq(JfInteractionProgress::getUserId, userId)
                .eq(JfInteractionProgress::getLessonId, lessonId));
    }

    @Override
    public Map<String, Map<String, Map<String, Object>>> listGrouped(Long userId, Integer lessonId) {
        Map<String, Map<String, Map<String, Object>>> result = new LinkedHashMap<>();
        result.put("words", new LinkedHashMap<>());
        result.put("sentences", new LinkedHashMap<>());
        result.put("grammarExamples", new LinkedHashMap<>());
        for (JfInteractionProgress p : listByLesson(userId, lessonId)) {
            String key = mapItemTypeKey(p.getItemType());
            Map<String, Map<String, Object>> bucket = result.computeIfAbsent(key, k -> new LinkedHashMap<>());
            Map<String, Object> entry = new HashMap<>();
            entry.put("pronunciationState", p.getPronunciationState());
            entry.put("skipped", p.getSkipped());
            if (p.getDetail() != null) entry.put("detail", p.getDetail());
            bucket.put(p.getItemId(), entry);
        }
        return result;
    }

    private String mapItemTypeKey(String itemType) {
        if ("word".equals(itemType)) return "words";
        if ("sentence".equals(itemType)) return "sentences";
        if ("grammarExample".equals(itemType)) return "grammarExamples";
        return itemType;
    }

    @Override
    public JfInteractionProgress upsert(Long userId, Integer lessonId, String itemType, String itemId, InteractionProgressUpdateVo vo) {
        JfInteractionProgress existing = mapper.selectOne(new LambdaQueryWrapper<JfInteractionProgress>()
                .eq(JfInteractionProgress::getUserId, userId)
                .eq(JfInteractionProgress::getLessonId, lessonId)
                .eq(JfInteractionProgress::getItemType, itemType)
                .eq(JfInteractionProgress::getItemId, itemId));

        boolean isNew = existing == null;
        if (isNew) {
            existing = new JfInteractionProgress();
            existing.setUserId(userId);
            existing.setLessonId(lessonId);
            existing.setItemType(itemType);
            existing.setItemId(itemId);
            existing.setPronunciationState("");
            existing.setSkipped(false);
        }
        if (vo.getPronunciationState() != null) existing.setPronunciationState(vo.getPronunciationState());
        if (vo.getSkipped() != null) existing.setSkipped(vo.getSkipped());
        if (vo.getDetail() != null) existing.setDetail(vo.getDetail());

        if (isNew) mapper.insert(existing);
        else mapper.updateById(existing);
        return existing;
    }

    @Override
    public void resetByLesson(Long userId, Integer lessonId) {
        mapper.delete(new LambdaQueryWrapper<JfInteractionProgress>()
                .eq(JfInteractionProgress::getUserId, userId)
                .eq(JfInteractionProgress::getLessonId, lessonId));
    }
}
