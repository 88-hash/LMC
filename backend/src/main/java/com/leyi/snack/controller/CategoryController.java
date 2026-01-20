package com.leyi.snack.controller;

import com.leyi.snack.common.Result;
import com.leyi.snack.entity.Category;
import com.leyi.snack.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public Result<List<Category>> list() {
        return Result.success(categoryService.findAll());
    }

    @PostMapping("/add")
    public Result<String> add(@RequestBody Category category) {
        categoryService.add(category);
        return Result.success("添加成功");
    }

    @PostMapping("/update")
    public Result<String> update(@RequestBody Category category) {
        categoryService.update(category);
        return Result.success("修改成功");
    }

    @GetMapping("/delete")
    public Result<String> delete(@RequestParam Long id) {
        categoryService.delete(id);
        return Result.success("删除成功");
    }
}
