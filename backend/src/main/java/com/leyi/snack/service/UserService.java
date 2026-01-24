package com.leyi.snack.service;

import com.leyi.snack.entity.AdminLoginVO;

public interface UserService {
    /**
     * 发送验证码
     */
    void sendCode(String phone);

    /**
     * 用户登录（手机号+验证码）
     */
    AdminLoginVO login(String phone, String code);

    /**
     * 更新用户信息
     */
    AdminLoginVO updateProfile(Long userId, String name, String avatar);
}
