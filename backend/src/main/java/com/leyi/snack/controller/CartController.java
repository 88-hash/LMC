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
import com.leyi.snack.dto.CartAddDTO;
import com.leyi.snack.dto.CartUpdateDTO;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;

    @PostMapping("/add")
    public Result<String> add(@RequestBody CartAddDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        System.out.println("CartController.add: userId=" + userId + ", dto=" + dto);
        try {
            if (dto.getGoodsId() == null || dto.getGoodsId() <= 0) {
                return Result.error("商品ID缺失");
            }
            cartService.addToCart(userId, dto);
            return Result.success("加入购物车成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/list")
    public Result<List<CartVO>> list() {
        Long userId = (Long) request.getAttribute("userId");
        System.out.println("CartController.list: userId=" + userId);
        List<CartVO> list = cartService.list(userId);
        System.out.println("CartController.list: result size=" + list.size());
        return Result.success(list);
    }

    @PostMapping("/update")
    public Result<String> update(@RequestBody CartUpdateDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.update(dto.getId(), dto.getQuantity());
        return Result.success("更新成功");
    }

    @GetMapping("/delete")
    public Result<String> delete(@RequestParam Long id) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.delete(id);
        return Result.success("删除成功");
    }
}
