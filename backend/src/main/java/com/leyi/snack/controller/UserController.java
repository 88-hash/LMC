package com.leyi.snack.controller;

import com.leyi.snack.common.Result;
import com.leyi.snack.entity.AdminLoginVO;
import com.leyi.snack.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<AdminLoginVO> login(@RequestParam String phone) {
        return Result.success(userService.login(phone));
    }
}
