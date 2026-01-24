package com.leyi.snack.service;

import com.leyi.snack.entity.Admin;
import com.leyi.snack.entity.AdminLoginDTO;
import com.leyi.snack.entity.AdminLoginVO;

import java.util.List;
import java.util.Map;

public interface AdminService {
    AdminLoginVO login(AdminLoginDTO adminLoginDTO);

    void addClerk(Map<String, Object> params);
    void updateClerk(Map<String, Object> params);
    void deleteClerk(Long id);
    List<Admin> listAdmins();
}
