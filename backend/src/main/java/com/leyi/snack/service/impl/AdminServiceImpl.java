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
        Admin admin = adminMapper.selectByPhone(adminLoginDTO.getPhone());
        if (admin == null) {
            throw new RuntimeException("账号不存在");
        }
        if (!admin.getPassword().equals(adminLoginDTO.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", admin.getId());
        claims.put("role", "admin");
        String token = jwtUtils.generateToken(claims);

        return AdminLoginVO.builder()
                .id(admin.getId())
                .name(admin.getName())
                .phone(admin.getPhone())
                .token(token)
                .build();
    }
}
