package com.example.demo.japaflow.service;

import com.example.demo.japaflow.entity.JfWrongBook;
import com.example.demo.japaflow.vo.WrongBookAddVo;

import java.util.List;

public interface WrongBookService {
    List<JfWrongBook> listByLesson(Long userId, Integer lessonId);

    JfWrongBook add(Long userId, Integer lessonId, WrongBookAddVo vo);

    void resolve(Long userId, Integer lessonId, String itemType, String itemId);

    void resetByLesson(Long userId, Integer lessonId);
}
