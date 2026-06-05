package com.example.demo.japaflow.service;

import com.example.demo.japaflow.entity.JfFavorite;
import com.example.demo.japaflow.vo.FavoriteAddVo;

import java.util.List;

public interface FavoriteService {
    List<JfFavorite> list(Long userId, Integer lessonId);

    JfFavorite add(Long userId, Integer lessonId, FavoriteAddVo vo);

    void remove(Long userId, Integer lessonId, String itemType, String itemId);

    void resetByLesson(Long userId, Integer lessonId);
}
