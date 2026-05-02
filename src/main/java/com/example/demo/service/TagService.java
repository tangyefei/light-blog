package com.example.demo.service;

import com.example.demo.entity.Tag;
import com.example.demo.vo.TagAddVo;

import java.util.List;

/**
 * 标签服务接口。
 */
public interface TagService {

    /**
     * 查询全部标签。
     *
     * @return 标签列表
     */
    List<Tag> findAll();

    /**
     * 新增标签。
     *
     * @param request 新增标签请求
     * @return 新标签 ID
     */
    Long add(TagAddVo request);
}
