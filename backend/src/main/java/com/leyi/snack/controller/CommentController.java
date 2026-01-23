package com.leyi.snack.controller;

import com.leyi.snack.common.Result;
import com.leyi.snack.entity.Comment;
import com.leyi.snack.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/add")
    public Result add(@RequestBody Map<String, Object> params) {
        try {
            Long orderItemId = Long.valueOf(params.get("orderItemId").toString());
            Integer rating = Integer.valueOf(params.get("rating").toString());
            String content = (String) params.get("content");
            String userPhone = (String) params.get("userPhone");
            
            commentService.addComment(orderItemId, rating, content, userPhone);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/list/goods")
    public Result<List<Comment>> listByGoods(@RequestParam Long goodsId) {
        return Result.success(commentService.getCommentsByGoodsId(goodsId));
    }

    @GetMapping("/list/user")
    public Result<List<Map<String, Object>>> listByUser(@RequestParam String userPhone) {
        return Result.success(commentService.getUserComments(userPhone));
    }
}
