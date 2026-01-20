package com.leyi.snack.service;

import com.leyi.snack.entity.AdminLoginDTO;
import com.leyi.snack.entity.AdminLoginVO;

public interface AdminService {
    /**
     * 管理员登录
     */
    AdminLoginVO login(AdminLoginDTO adminLoginDTO);
}
