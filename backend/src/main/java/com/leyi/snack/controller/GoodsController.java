package com.leyi.snack.controller;

import com.leyi.snack.common.Result;
import com.leyi.snack.entity.Goods;
import com.leyi.snack.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @GetMapping("/list")
    public Result<List<Goods>> list() {
        return Result.success(goodsService.findAll());
    }

    @PostMapping("/add")
    public Result<String> add(@RequestBody Goods goods) {
        goodsService.add(goods);
        return Result.success("添加成功");
    }

    @PostMapping("/update")
    public Result<String> update(@RequestBody Goods goods) {
        goodsService.update(goods);
        return Result.success("修改成功");
    }

    @GetMapping("/delete")
    public Result<String> delete(@RequestParam Long id) {
        goodsService.delete(id);
        return Result.success("删除成功");
    }
}
