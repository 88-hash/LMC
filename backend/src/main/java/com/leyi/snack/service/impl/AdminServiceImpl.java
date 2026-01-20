package com.leyi.snack.service.impl;

import com.leyi.snack.entity.Admin;
import com.leyi.snack.entity.AdminLoginDTO;
import com.leyi.snack.entity.AdminLoginVO;
import com.leyi.snack.mapper.AdminMapper;
import com.leyi.snack.service.AdminService;
import com.leyi.snack.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public AdminLoginVO login(AdminLoginDTO adminLoginDTO) {
        String phone = adminLoginDTO.getPhone();
        String password = adminLoginDTO.getPassword();

        // 1. 查询用户
        Admin admin = adminMapper.selectByPhone(phone);

        // 2. 校验账号是否存在
        if (admin == null) {
            throw new RuntimeException("账号不存在");
        }

        // 3. 校验密码 (实际项目中建议加密存储，这里演示直接比对)
        if (!admin.getPassword().equals(password)) {
            throw new RuntimeException("密码错误");
        }

        // 4. 生成 Token
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", admin.getId());
        claims.put("role", admin.getRole());
        String token = jwtUtils.generateToken(claims);

        // 5. 封装返回结果
        return AdminLoginVO.builder()
                .id(admin.getId())
                .name(admin.getName())
                .token(token)
                .build();
    }
}
