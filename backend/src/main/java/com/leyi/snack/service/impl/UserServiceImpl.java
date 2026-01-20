package com.leyi.snack.service.impl;

import com.leyi.snack.entity.AdminLoginVO;
import com.leyi.snack.entity.User;
import com.leyi.snack.mapper.UserMapper;
import com.leyi.snack.service.UserService;
import com.leyi.snack.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public AdminLoginVO login(String phone) {
        // 1. 查询用户
        User user = userMapper.selectByPhone(phone);

        // 2. 如果不存在，自动注册
        if (user == null) {
            user = new User();
            user.setPhone(phone);
            // 默认用户名: 用户+手机后4位
            String suffix = phone.length() >= 4 ? phone.substring(phone.length() - 4) : phone;
            user.setName("用户" + suffix);
            user.setAvatar(""); // 默认头像留空
            userMapper.save(user);
        } else {
            // 更新活跃时间
            userMapper.update(user);
        }

        // 3. 生成 Token
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("role", "user"); // 角色标识
        String token = jwtUtils.generateToken(claims);

        // 4. 返回结果
        return AdminLoginVO.builder()
                .id(user.getId())
                .name(user.getName())
                .token(token)
                .build();
    }
}
