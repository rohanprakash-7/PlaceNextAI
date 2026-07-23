package com.placenextai.controller;

import com.placenextai.dto.AlumniRegisterRequest;
import com.placenextai.dto.AuthResponse;
import com.placenextai.dto.LoginRequest;
import com.placenextai.dto.MeResponse;
import com.placenextai.dto.RecruiterRegisterRequest;
import com.placenextai.dto.StudentRegisterRequest;
import com.placenextai.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ---------- Role-scoped endpoints (Phase 1 spec) ----------

    @PostMapping("/student/register")
    public ResponseEntity<AuthResponse> registerStudent(@Valid @RequestBody StudentRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerStudent(request));
    }

    @PostMapping("/student/login")
    public ResponseEntity<AuthResponse> loginStudent(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request, "ROLE_STUDENT"));
    }

    @PostMapping("/recruiter/register")
    public ResponseEntity<AuthResponse> registerRecruiter(@Valid @RequestBody RecruiterRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerRecruiter(request));
    }

    @PostMapping("/recruiter/login")
    public ResponseEntity<AuthResponse> loginRecruiter(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request, "ROLE_RECRUITER"));
    }

    @PostMapping("/alumni/register")
    public ResponseEntity<AuthResponse> registerAlumni(@Valid @RequestBody AlumniRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerAlumni(request));
    }

    @PostMapping("/alumni/login")
    public ResponseEntity<AuthResponse> loginAlumni(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request, "ROLE_ALUMNI"));
    }

    @PostMapping("/admin/login")
    public ResponseEntity<AuthResponse> loginAdmin(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request, "ROLE_ADMIN"));
    }

    // ---------- Generic endpoints (used by the role-detecting login form) ----------

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody StudentRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerStudent(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(Authentication authentication) {
        return ResponseEntity.ok(authService.getCurrentUser(authentication.getName()));
    }
}
