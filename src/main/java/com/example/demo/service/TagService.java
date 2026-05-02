package com.example.demo.service;

import com.example.demo.vo.TagAddVo;

/**
 * 标签服务接口。
 */
public interface TagService {

    /**
     * 新增标签。
     *
     * @param request 新增标签请求
     * @return 新标签 ID
     */
    Long add(TagAddVo request);
}
