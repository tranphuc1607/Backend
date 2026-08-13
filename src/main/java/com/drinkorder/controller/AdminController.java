package com.drinkorder.controller;

import com.drinkorder.dto.admin.DashboardStats;
import com.drinkorder.dto.common.ApiResponse;
import com.drinkorder.dto.user.UserResponse;
import com.drinkorder.service.AdminService;
import com.drinkorder.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller Admin - Quản lý người dùng và thống kê.
 * Base URL: /api/v1/admin
 * Tất cả các API cần quyền ADMIN.
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;

    // GET /admin/dashboard → Thống kê tổng quan
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardStats>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.ok(adminService.getDashboardStats()));
    }

    // GET /admin/users → Danh sách tất cả người dùng
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.ok(userService.getAllUsers()));
    }

    // DELETE /admin/users/{id} → Vô hiệu hóa tài khoản user (disable)
    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Void>> disableUser(@PathVariable Long id) {
        userService.disableUser(id);
        return ResponseEntity.ok(ApiResponse.ok("Đã vô hiệu hóa tài khoản", null));
    }
}
