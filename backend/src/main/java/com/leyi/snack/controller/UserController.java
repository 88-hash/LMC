package com.leyi.snack.controller;

import com.leyi.snack.common.Result;
import com.leyi.snack.entity.AdminLoginVO;
import com.leyi.snack.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/code")
    public Result<String> sendCode(@RequestBody Map<String, String> map) {
        String phone = map.get("phone");
        if (phone == null || phone.isEmpty()) {
            return Result.error("手机号不能为空");
        }
        userService.sendCode(phone);
        return Result.success("发送成功");
    }

    @PostMapping("/login")
    public Result<AdminLoginVO> login(@RequestBody Map<String, String> map) {
        try {
            String phone = map.get("phone");
            String code = map.get("code");
            return Result.success(userService.login(phone, code));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
