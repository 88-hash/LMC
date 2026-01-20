package com.leyi.snack.controller;

import com.leyi.snack.common.Result;
import com.leyi.snack.entity.Order;
import com.leyi.snack.service.OrderService;
import com.leyi.snack.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private HttpServletRequest request;

    // 复用鉴权逻辑 (后期可提取到 BaseController 或拦截器)
    private Long getUserId() {
        String token = request.getHeader("Authorization");
        if (StringUtils.hasText(token)) {
            try {
                if (token.startsWith("Bearer ")) token = token.substring(7);
                Claims claims = jwtUtils.parseToken(token);
                Object idObj = claims.get("id");
                if (idObj instanceof Integer) return ((Integer) idObj).longValue();
                else if (idObj instanceof Long) return (Long) idObj;
            } catch (Exception e) {}
        }
        throw new RuntimeException("请先登录");
    }

    @PostMapping("/create")
    public Result<String> create(@RequestBody Map<String, String> params) {
        Long userId = getUserId();
        String remark = params.get("remark");
        String orderNo = orderService.createOrder(userId, remark);
        return Result.success(orderNo);
    }

    @GetMapping("/list")
    public Result<List<Order>> list() {
        Long userId = getUserId();
        return Result.success(orderService.list(userId));
    }

    @GetMapping("/detail")
    public Result<Map<String, Object>> detail(@RequestParam Long id) {
        // 这里暂时不校验 userId 是否匹配，实际项目建议校验
        getUserId(); 
        return Result.success(orderService.detail(id));
    }
}
