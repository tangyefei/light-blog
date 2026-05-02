package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.common.BusinessException;
import com.example.demo.common.ResultCode;
import com.example.demo.entity.Tag;
import com.example.demo.mapper.TagMapper;
import com.example.demo.service.TagService;
import com.example.demo.vo.TagAddVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 标签服务实现类。
 */
@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagMapper tagMapper;

    @Override
    public Long add(TagAddVo request) {
        String name = request.getName().trim();

        Long count = tagMapper.selectCount(new LambdaQueryWrapper<Tag>().eq(Tag::getName, name));
        if (count > 0) {
            throw new BusinessException(ResultCode.CONFLICT, "标签已存在");
        }

        Tag tag = new Tag();
        tag.setName(name);
        tagMapper.insert(tag);
        return tag.getId();
    }
}
