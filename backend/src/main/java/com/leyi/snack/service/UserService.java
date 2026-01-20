package com.leyi.snack.service;

import com.leyi.snack.entity.AdminLoginVO;

public interface UserService {
    /**
     * 用户登录（手机号自动注册）
     * 复用 AdminLoginVO 返回 token
     */
    AdminLoginVO login(String phone);
}
