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
import com.leyi.snack.dto.OrderCreateDTO;

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
        try {
            // ⚠️ 强制修改：不再检查 Token，直接模拟 1 号用户下单
            Long userId = 1L;
            
            String remark = params.get("remark");
            // 调用 Service
            String orderNo = orderService.createOrder(userId, remark);
            return Result.success(orderNo);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("下单失败: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public Result<List<Order>> list() {
        // ⚠️ 强制修改：直接查看 1 号用户的订单
        Long userId = 1L;
        return Result.success(orderService.list(userId));
    }

    @GetMapping("/detail")
    public Result<Map<String, Object>> detail(@RequestParam Long id) {
        // 这里暂时不校验 userId 是否匹配，实际项目建议校验
        Long userId = 1L; // 临时模拟用户
        return Result.success(orderService.detail(id));
    }
}
