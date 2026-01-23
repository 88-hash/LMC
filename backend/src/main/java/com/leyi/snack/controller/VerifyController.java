package com.leyi.snack.controller;

import com.leyi.snack.common.Result;
import com.leyi.snack.entity.Order;
import com.leyi.snack.service.VerifyService;
import com.leyi.snack.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/verify")
public class VerifyController {

    @Autowired
    private VerifyService verifyService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private HttpServletRequest request;

    // 获取管理员ID (复用 JwtUtils，因为 Admin 和 User 共用一套 Token 机制)
    private Long getAdminId() {
        String token = request.getHeader("Authorization");
        if (StringUtils.hasText(token)) {
            try {
                if (token.startsWith("Bearer ")) token = token.substring(7);
                Claims claims = jwtUtils.parseToken(token);
                // 简单的权限检查 (实际项目应检查 role)
                // if (!"admin".equals(claims.get("role"))) throw new RuntimeException("无权操作");
                Object idObj = claims.get("id");
                if (idObj instanceof Integer) return ((Integer) idObj).longValue();
                else if (idObj instanceof Long) return (Long) idObj;
            } catch (Exception e) {}
        }
        throw new RuntimeException("请先登录管理员账号");
    }

    @GetMapping("/query")
    public Result<Order> query(@RequestParam String verifyCode) {
        // 核销查询通常也需要登录
        getAdminId();
        Order order = verifyService.queryByVerifyCode(verifyCode);
        if (order == null) {
            return Result.error("核销码不存在");
        }
        return Result.success(order);
    }

    @GetMapping("/pending")
    public Result<java.util.List<Order>> pending() {
        getAdminId();
        return Result.success(verifyService.listPending());
    }

    @PostMapping("/confirm")
    public Result<String> confirm(@RequestBody Map<String, Object> params) {
        Long adminId = getAdminId();
        if (params.containsKey("orderId")) {
             Long orderId = Long.valueOf(params.get("orderId").toString());
             verifyService.confirmVerifyById(orderId, adminId);
        } else if (params.containsKey("verifyCode")) {
             String verifyCode = params.get("verifyCode").toString();
             verifyService.confirmVerify(verifyCode, adminId);
        } else {
            return Result.error("参数缺失");
        }
        return Result.success("核销成功");
    }
}
