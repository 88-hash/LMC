package com.leyi.snack.service;

import com.leyi.snack.entity.Category;
import java.util.List;

public interface CategoryService {
    List<Category> findAll();
    List<Category> findLevel1();
    List<Category> findByParentId(Long parentId);
    void add(Category category);
    void update(Category category);
    void delete(Long id, Long parentId);
}
