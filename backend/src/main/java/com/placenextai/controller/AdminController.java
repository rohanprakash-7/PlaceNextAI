package com.placenextai.controller;

import com.placenextai.dto.AdminDashboardResponse;
import com.placenextai.dto.AuthResponse;
import com.placenextai.dto.LoginRequest;
import com.placenextai.service.AdminService;
import com.placenextai.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AuthService authService;
    private final AdminService adminService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request, "ROLE_ADMIN"));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardResponse> dashboard() {
        return ResponseEntity.ok(adminService.getDashboard());
    }
}
