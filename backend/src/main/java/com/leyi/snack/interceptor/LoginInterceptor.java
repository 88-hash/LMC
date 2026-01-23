package com.leyi.snack.interceptor;

import com.leyi.snack.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行 OPTIONS 请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("Authorization");
        try {
            if (token != null && !token.isEmpty()) {
                Claims claims = jwtUtils.parseToken(token);
                if (claims != null) {
                    Long userId = Long.valueOf(claims.get("id").toString());
                    request.setAttribute("userId", userId);
                    return true;
                }
            }
        } catch (Exception e) {
            // 解析失败
        }

        // 认证失败，返回 401
        response.setStatus(401);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":0, \"message\":\"Unauthorized\"}");
        return false;
    }
}