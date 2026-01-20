package com.leyi.snack.service;

import com.leyi.snack.entity.Category;
import java.util.List;

public interface CategoryService {
    List<Category> findAll();
    void add(Category category);
    void update(Category category);
    void delete(Long id);
}
