package com.example.demo.japaflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.japaflow.entity.JfFavorite;
import com.example.demo.japaflow.mapper.JfFavoriteMapper;
import com.example.demo.japaflow.service.FavoriteService;
import com.example.demo.japaflow.vo.FavoriteAddVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final JfFavoriteMapper mapper;

    @Override
    public List<JfFavorite> list(Long userId, Integer lessonId) {
        LambdaQueryWrapper<JfFavorite> q = new LambdaQueryWrapper<JfFavorite>()
                .eq(JfFavorite::getUserId, userId);
        if (lessonId != null) q.eq(JfFavorite::getLessonId, lessonId);
        return mapper.selectList(q);
    }

    @Override
    public JfFavorite add(Long userId, Integer lessonId, FavoriteAddVo vo) {
        JfFavorite existing = mapper.selectOne(new LambdaQueryWrapper<JfFavorite>()
                .eq(JfFavorite::getUserId, userId)
                .eq(JfFavorite::getLessonId, lessonId)
                .eq(JfFavorite::getItemType, vo.getItemType())
                .eq(JfFavorite::getItemId, vo.getItemId()));

        boolean isNew = existing == null;
        if (isNew) {
            existing = new JfFavorite();
            existing.setUserId(userId);
            existing.setLessonId(lessonId);
            existing.setItemType(vo.getItemType());
            existing.setItemId(vo.getItemId());
            existing.setSavedAt(LocalDateTime.now());
        }
        existing.setSnapshot(vo.getSnapshot());

        if (isNew) mapper.insert(existing);
        else mapper.updateById(existing);
        return existing;
    }

    @Override
    public void remove(Long userId, Integer lessonId, String itemType, String itemId) {
        mapper.delete(new LambdaQueryWrapper<JfFavorite>()
                .eq(JfFavorite::getUserId, userId)
                .eq(JfFavorite::getLessonId, lessonId)
                .eq(JfFavorite::getItemType, itemType)
                .eq(JfFavorite::getItemId, itemId));
    }

    @Override
    public void resetByLesson(Long userId, Integer lessonId) {
        mapper.delete(new LambdaQueryWrapper<JfFavorite>()
                .eq(JfFavorite::getUserId, userId)
                .eq(JfFavorite::getLessonId, lessonId));
    }
}
