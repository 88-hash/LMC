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
    private HttpServletRequest request;

    @PostMapping("/create")
    public Result<String> create(@RequestBody Map<String, String> params) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            
            String remark = params.get("remark");
            // 调用 Service
            String orderNo = orderService.createOrder(userId, remark);
            return Result.success(orderNo);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("下单失败: " + e.getMessage());
        }
    }

    @PostMapping("/pay")
    public Result<String> pay(@RequestBody Map<String, String> params) {
        Long userId = (Long) request.getAttribute("userId");
        String orderNo = params.get("orderNo");
        String payMethod = params.get("payMethod");
        try {
            orderService.pay(orderNo, payMethod, userId);
            return Result.success("支付成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/list")
    public Result<List<Order>> list() {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(orderService.list(userId));
    }

    @GetMapping("/detail")
    public Result<Map<String, Object>> detail(@RequestParam Long id) {
        Long currentUserId = (Long) request.getAttribute("userId");
        Map<String, Object> data = orderService.detail(id);
        Order order = (Order) data.get("order");
        if (order != null && !order.getUserId().equals(currentUserId)) {
            return Result.error("无权访问");
        }
        return Result.success(data);
    }

    @GetMapping("/getByNo")
    public Result<Order> getByNo(@RequestParam String orderNo) {
        Order order = orderService.getByOrderNo(orderNo);
        if (order == null) {
            return Result.error("订单不存在");
        }
        // 安全校验：只能查自己的订单 (除非是管理员)
        Long currentUserId = (Long) request.getAttribute("userId");
        if (!order.getUserId().equals(currentUserId)) {
            return Result.error("无权访问");
        }
        return Result.success(order);
    }
}
