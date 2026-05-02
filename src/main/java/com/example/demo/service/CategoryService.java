package com.example.demo.service;

import com.example.demo.entity.Category;

import java.util.List;

/**
 * 分类服务接口。
 */
public interface CategoryService {

    /**
     * 查询全部分类。
     *
     * @return 分类列表
     */
    List<Category> findAll();
}
