package com.leyi.snack.service.impl;

import com.leyi.snack.entity.Admin;
import com.leyi.snack.entity.AdminLoginDTO;
import com.leyi.snack.entity.AdminLoginVO;
import com.leyi.snack.mapper.AdminMapper;
import com.leyi.snack.service.AdminService;
import com.leyi.snack.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
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
        claims.put("role", admin.getRole());
        String token = jwtUtils.generateToken(claims);

        return AdminLoginVO.builder()
                .id(admin.getId())
                .name(admin.getName())
                .phone(admin.getPhone())
                .role(admin.getRole())
                .token(token)
                .build();
    }

    @Override
    public void addClerk(Map<String, Object> params) {
        String phone = (String) params.get("phone");
        if (adminMapper.selectByPhone(phone) != null) {
            throw new RuntimeException("手机号已存在");
        }

        Admin admin = new Admin();
        admin.setName((String) params.get("name"));
        admin.setPhone(phone);
        admin.setPassword((String) params.get("password"));
        admin.setRole("clerk");
        admin.setCreatedAt(LocalDateTime.now());
        adminMapper.insert(admin);
    }

    @Override
    public void updateClerk(Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        Admin admin = adminMapper.selectById(id);
        if (admin == null) {
            throw new RuntimeException("账号不存在");
        }

        if (params.get("name") != null) admin.setName((String) params.get("name"));
        if (params.get("phone") != null) admin.setPhone((String) params.get("phone"));
        if (params.get("password") != null && !params.get("password").toString().isEmpty()) {
            admin.setPassword((String) params.get("password"));
        }
        
        admin.setUpdatedAt(LocalDateTime.now());
        adminMapper.update(admin);
    }

    @Override
    public void deleteClerk(Long id) {
        adminMapper.deleteById(id);
    }

    @Override
    public List<Admin> listAdmins() {
        return adminMapper.selectAll();
    }
}
