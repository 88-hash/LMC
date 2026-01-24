package com.leyi.snack.controller;

import com.leyi.snack.common.Result;
import com.leyi.snack.entity.Admin;
import com.leyi.snack.entity.AdminLoginDTO;
import com.leyi.snack.entity.AdminLoginVO;
import com.leyi.snack.service.AdminService;
import com.leyi.snack.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/admin/login")
    public Result<AdminLoginVO> login(@RequestBody AdminLoginDTO adminLoginDTO) {
        try {
            AdminLoginVO adminLoginVO = adminService.login(adminLoginDTO);
            return Result.success(adminLoginVO);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // --- Clerk Management (Manager Only) ---

    @GetMapping("/admin/auth/list")
    public Result<List<Admin>> list(HttpServletRequest request) {
        List<Admin> admins = adminService.listAdmins();
        if (admins != null) {
            admins.forEach(admin -> admin.setPassword(null));
        }
        return Result.success(admins);
    }

    @PostMapping("/admin/auth/clerk/add")
    public Result addClerk(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        if (!checkManagerRole(request)) return Result.error("无权操作");
        try {
            adminService.addClerk(params);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/admin/auth/clerk/update")
    public Result updateClerk(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        if (!checkManagerRole(request)) return Result.error("无权操作");
        try {
            adminService.updateClerk(params);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/admin/auth/clerk/delete")
    public Result deleteClerk(@RequestParam Long id, HttpServletRequest request) {
        if (!checkManagerRole(request)) return Result.error("无权操作");
        try {
            adminService.deleteClerk(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    private boolean checkManagerRole(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (token == null) return false;
        try {
            Claims claims = jwtUtils.parseToken(token);
            String role = (String) claims.get("role");
            return "manager".equals(role) || "admin".equals(role); // Compatible with 'admin' if used
        } catch (Exception e) {
            return false;
        }
    }
}
