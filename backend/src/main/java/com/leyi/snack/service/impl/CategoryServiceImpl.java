package com.leyi.snack.service.impl;

import com.leyi.snack.entity.Category;
import com.leyi.snack.mapper.CategoryMapper;
import com.leyi.snack.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public List<Category> findLevel1() {
        return categoryMapper.findLevel1();
    }

    @Override
    public List<Category> findByParentId(Long parentId) {
        if (parentId == null || parentId <= 0) {
            return new ArrayList<>();
        }
        return categoryMapper.findLevel2ByParentId(parentId);
    }

    @Override
    public void add(Category category) {
        validateBasic(category);
        if (category.getParentId() == null || category.getParentId() == 0L) {
            checkLevel1NameUnique(category.getName(), null);
            categoryMapper.saveLevel1(category);
            return;
        }
        ensureLevel1Exists(category.getParentId());
        checkLevel2NameUnique(category.getParentId(), category.getName(), null);
        categoryMapper.saveLevel2(category);
    }

    @Override
    public void update(Category category) {
        validateBasic(category);
        if (category.getId() == null || category.getId() <= 0) {
            throw new RuntimeException("分类ID无效");
        }

        if (category.getParentId() == null || category.getParentId() == 0L) {
            Category old = categoryMapper.findLevel1ById(category.getId());
            if (old == null) {
                throw new RuntimeException("一级分类不存在");
            }
            checkLevel1NameUnique(category.getName(), category.getId());
            categoryMapper.updateLevel1(category);
            return;
        }

        ensureLevel1Exists(category.getParentId());
        Category old = categoryMapper.findLevel2ById(category.getId());
        if (old == null) {
            throw new RuntimeException("二级分类不存在");
        }
        checkLevel2NameUnique(category.getParentId(), category.getName(), category.getId());
        categoryMapper.updateLevel2(category);
    }

    @Override
    public void delete(Long id, Long parentId) {
        if (id == null || id <= 0) {
            throw new RuntimeException("分类ID无效");
        }

        if (parentId != null) {
            if (parentId <= 0) {
                deleteLevel1(id);
            } else {
                deleteLevel2(id, parentId);
            }
            return;
        }

        // 兼容旧接口：未传 parentId 时，优先尝试删除二级分类
        Category level2 = categoryMapper.findLevel2ById(id);
        if (level2 != null) {
            deleteLevel2(id, level2.getParentId());
            return;
        }
        deleteLevel1(id);
    }

    private void deleteLevel1(Long id) {
        Category level1 = categoryMapper.findLevel1ById(id);
        if (level1 == null) {
            throw new RuntimeException("一级分类不存在");
        }
        int childCount = categoryMapper.countLevel2ByParentId(id);
        if (childCount > 0) {
            throw new RuntimeException("请先删除该一级分类下的二级分类");
        }
        categoryMapper.deleteLevel1ById(id);
    }

    private void deleteLevel2(Long id, Long parentId) {
        Category level2 = categoryMapper.findLevel2ById(id);
        if (level2 == null || !level2.getParentId().equals(parentId)) {
            throw new RuntimeException("二级分类不存在");
        }
        int goodsCount = categoryMapper.countGoodsByCategory2Id(id);
        if (goodsCount > 0) {
            throw new RuntimeException("该二级分类下仍有商品，不能删除");
        }
        categoryMapper.deleteLevel2ById(id);
    }

    private void validateBasic(Category category) {
        if (category == null) {
            throw new RuntimeException("分类参数不能为空");
        }
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new RuntimeException("分类名称不能为空");
        }
        category.setName(category.getName().trim());
        if (category.getSort() == null) {
            category.setSort(0);
        }
    }

    private void ensureLevel1Exists(Long parentId) {
        Category parent = categoryMapper.findLevel1ById(parentId);
        if (parent == null) {
            throw new RuntimeException("上级一级分类不存在");
        }
    }

    private void checkLevel1NameUnique(String name, Long selfId) {
        Category exists = categoryMapper.findLevel1ByName(name);
        if (exists != null && (selfId == null || !exists.getId().equals(selfId))) {
            throw new RuntimeException("一级分类名称已存在");
        }
    }

    private void checkLevel2NameUnique(Long parentId, String name, Long selfId) {
        Category exists = categoryMapper.findLevel2ByNameAndParentId(name, parentId);
        if (exists != null && (selfId == null || !exists.getId().equals(selfId))) {
            throw new RuntimeException("同一一级分类下二级分类名称已存在");
        }
    }
}

