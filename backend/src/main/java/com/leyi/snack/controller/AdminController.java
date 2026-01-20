package com.leyi.snack.controller;

import com.leyi.snack.common.Result;
import com.leyi.snack.entity.AdminLoginDTO;
import com.leyi.snack.entity.AdminLoginVO;
import com.leyi.snack.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/admin/login")
    public Result<AdminLoginVO> login(@RequestBody AdminLoginDTO adminLoginDTO) {
        try {
            AdminLoginVO adminLoginVO = adminService.login(adminLoginDTO);
            return Result.success(adminLoginVO);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
