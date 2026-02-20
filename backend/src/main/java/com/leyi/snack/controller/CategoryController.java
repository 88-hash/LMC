package com.leyi.snack.controller;

import com.leyi.snack.common.Result;
import com.leyi.snack.entity.Category;
import com.leyi.snack.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/category/list")
    public Result<List<Category>> list() {
        return Result.success(categoryService.findAll());
    }

    @GetMapping("/category1")
    public Result<List<Category>> category1() {
        return Result.success(categoryService.findLevel1());
    }

    @GetMapping("/category2")
    public Result<List<Category>> category2(@RequestParam Long parentId) {
        return Result.success(categoryService.findByParentId(parentId));
    }

    @PostMapping("/category/add")
    public Result<String> add(@RequestBody Category category) {
        categoryService.add(category);
        return Result.success("添加成功");
    }

    @PostMapping("/category/update")
    public Result<String> update(@RequestBody Category category) {
        categoryService.update(category);
        return Result.success("修改成功");
    }

    @GetMapping("/category/delete")
    public Result<String> delete(@RequestParam Long id, @RequestParam(required = false) Long parentId) {
        categoryService.delete(id, parentId);
        return Result.success("删除成功");
    }
}
