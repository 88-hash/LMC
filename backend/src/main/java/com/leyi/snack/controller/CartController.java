package com.leyi.snack.controller;

import com.leyi.snack.common.Result;
import com.leyi.snack.entity.CartVO;
import com.leyi.snack.service.CartService;
import com.leyi.snack.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private HttpServletRequest request;

    // 辅助方法：解析 Token 获取 userId
    private Long getUserId() {
        String token = request.getHeader("Authorization");
        if (StringUtils.hasText(token)) {
            try {
                // 去掉 "Bearer " 前缀（如果前端传了的话）
                if (token.startsWith("Bearer ")) {
                    token = token.substring(7);
                }
                Claims claims = jwtUtils.parseToken(token);
                // 注意：jjwt 解析数字可能会变成 Integer，需安全转换
                Object idObj = claims.get("id");
                if (idObj instanceof Integer) {
                    return ((Integer) idObj).longValue();
                } else if (idObj instanceof Long) {
                    return (Long) idObj;
                }
            } catch (Exception e) {
                // Token 无效或过期
                throw new RuntimeException("请先登录");
            }
        }
        throw new RuntimeException("请先登录");
    }

    @PostMapping("/add")
    public Result<String> add(@RequestBody Map<String, Object> params) {
        Long userId = getUserId();
        Long goodsId = Long.valueOf(params.get("goodsId").toString());
        Integer quantity = Integer.valueOf(params.get("quantity").toString());
        
        cartService.add(userId, goodsId, quantity);
        return Result.success("加入购物车成功");
    }

    @GetMapping("/list")
    public Result<List<CartVO>> list() {
        Long userId = getUserId();
        return Result.success(cartService.list(userId));
    }

    @PostMapping("/update")
    public Result<String> update(@RequestBody Map<String, Object> params) {
        // 鉴权检查
        getUserId();
        Long id = Long.valueOf(params.get("id").toString());
        Integer quantity = Integer.valueOf(params.get("quantity").toString());
        
        cartService.update(id, quantity);
        return Result.success("更新成功");
    }

    @GetMapping("/delete")
    public Result<String> delete(@RequestParam Long id) {
        // 鉴权检查
        getUserId();
        cartService.delete(id);
        return Result.success("删除成功");
    }
}
