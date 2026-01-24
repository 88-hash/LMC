package com.leyi.snack.service.impl;

import com.leyi.snack.entity.AdminLoginVO;
import com.leyi.snack.entity.User;
import com.leyi.snack.mapper.UserMapper;
import com.leyi.snack.service.UserService;
import com.leyi.snack.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;

@Service
public class UserServiceImpl implements UserService {

    // 模拟验证码缓存 (生产环境应使用 Redis)
    private static final Map<String, String> codeCache = new ConcurrentHashMap<>();

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public void sendCode(String phone) {
        // 生成6位随机验证码
        String code = String.valueOf(new Random().nextInt(899999) + 100000);
        codeCache.put(phone, code);
        
        // 模拟短信发送
        System.out.println("【模拟短信】手机号 " + phone + " 的验证码是：" + code);
    }

    @Override
    public AdminLoginVO updateProfile(Long userId, String name, String avatar) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (name != null && !name.isEmpty()) {
            user.setName(name);
        }
        if (avatar != null && !avatar.isEmpty()) {
            user.setAvatar(avatar);
        }

        userMapper.update(user);

        // 返回最新的用户信息，方便前端更新
        return AdminLoginVO.builder()
                .id(user.getId())
                .name(user.getName())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .token(null) // 更新资料不需要返回 token
                .build();
    }

    @Override
    public AdminLoginVO login(String phone, String code) {
        // 0. 校验验证码
        String cachedCode = codeCache.get(phone);
        if (cachedCode == null || !cachedCode.equals(code)) {
            throw new RuntimeException("验证码错误或已过期");
        }
        // 验证通过后移除验证码 (防止重复使用)
        codeCache.remove(phone);

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
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .token(token)
                .build();
    }
}
