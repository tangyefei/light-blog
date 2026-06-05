package com.example.demo.japaflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.common.BusinessException;
import com.example.demo.common.ResultCode;
import com.example.demo.japaflow.entity.JfWrongBook;
import com.example.demo.japaflow.mapper.JfWrongBookMapper;
import com.example.demo.japaflow.service.WrongBookService;
import com.example.demo.japaflow.vo.WrongBookAddVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WrongBookServiceImpl implements WrongBookService {

    private final JfWrongBookMapper mapper;

    @Override
    public List<JfWrongBook> listByLesson(Long userId, Integer lessonId) {
        return mapper.selectList(new LambdaQueryWrapper<JfWrongBook>()
                .eq(JfWrongBook::getUserId, userId)
                .eq(JfWrongBook::getLessonId, lessonId));
    }

    @Override
    public JfWrongBook add(Long userId, Integer lessonId, WrongBookAddVo vo) {
        JfWrongBook existing = mapper.selectOne(new LambdaQueryWrapper<JfWrongBook>()
                .eq(JfWrongBook::getUserId, userId)
                .eq(JfWrongBook::getLessonId, lessonId)
                .eq(JfWrongBook::getItemType, vo.getItemType())
                .eq(JfWrongBook::getItemId, vo.getItemId()));

        boolean isNew = existing == null;
        if (isNew) {
            existing = new JfWrongBook();
            existing.setUserId(userId);
            existing.setLessonId(lessonId);
            existing.setItemType(vo.getItemType());
            existing.setItemId(vo.getItemId());
            existing.setResolved(false);
        }
        existing.setWrongDetail(vo.getWrongDetail());
        existing.setResolved(false);
        existing.setResolvedAt(null);

        if (isNew) mapper.insert(existing);
        else mapper.updateById(existing);
        return existing;
    }

    @Override
    public void resolve(Long userId, Integer lessonId, String itemType, String itemId) {
        JfWrongBook existing = mapper.selectOne(new LambdaQueryWrapper<JfWrongBook>()
                .eq(JfWrongBook::getUserId, userId)
                .eq(JfWrongBook::getLessonId, lessonId)
                .eq(JfWrongBook::getItemType, itemType)
                .eq(JfWrongBook::getItemId, itemId));
        if (existing == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "错题不存在");
        }
        existing.setResolved(true);
        existing.setResolvedAt(LocalDateTime.now());
        mapper.updateById(existing);
    }

    @Override
    public void resetByLesson(Long userId, Integer lessonId) {
        mapper.delete(new LambdaQueryWrapper<JfWrongBook>()
                .eq(JfWrongBook::getUserId, userId)
                .eq(JfWrongBook::getLessonId, lessonId));
    }
}
