package com.leyi.snack.service.impl;

import com.leyi.snack.entity.Category;
import com.leyi.snack.mapper.CategoryMapper;
import com.leyi.snack.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public List<Category> findAll() {
        return categoryMapper.findAll();
    }

    @Override
    public void add(Category category) {
        if (category.getParentId() == null) {
            category.setParentId(0L); // 默认为一级分类
        }
        if (category.getSort() == null) {
            category.setSort(0);
        }
        categoryMapper.save(category);
    }

    @Override
    public void update(Category category) {
        categoryMapper.update(category);
    }

    @Override
    public void delete(Long id) {
        categoryMapper.deleteById(id);
    }
}
