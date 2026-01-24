package com.leyi.snack.controller;

import com.leyi.snack.common.Result;
import com.leyi.snack.entity.AdminLoginVO;
import com.leyi.snack.service.UserService;
import com.leyi.snack.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/code")
    public Result sendCode(@RequestBody Map<String, String> params) {
        String phone = params.get("phone");
        userService.sendCode(phone);
        return Result.success();
    }

    @PostMapping("/login")
    public Result<AdminLoginVO> login(@RequestBody Map<String, String> params) {
        String phone = params.get("phone");
        String code = params.get("code");
        try {
            AdminLoginVO vo = userService.login(phone, code);
            return Result.success(vo);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/update")
    public Result<AdminLoginVO> update(@RequestBody Map<String, String> params, HttpServletRequest request) {
        try {
            String token = request.getHeader("token");
            Claims claims = jwtUtils.parseToken(token);
            Long userId = Long.valueOf(claims.get("id").toString());

            String name = params.get("name");
            String avatar = params.get("avatar");
            
            AdminLoginVO vo = userService.updateProfile(userId, name, avatar);
            return Result.success(vo);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
