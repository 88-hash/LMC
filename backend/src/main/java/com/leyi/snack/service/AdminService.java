package com.leyi.snack.service;

import com.leyi.snack.entity.AdminLoginDTO;
import com.leyi.snack.entity.AdminLoginVO;

public interface AdminService {
    AdminLoginVO login(AdminLoginDTO adminLoginDTO);
}
