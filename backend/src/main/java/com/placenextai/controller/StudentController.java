package com.placenextai.controller;

import com.placenextai.dto.*;
import com.placenextai.service.AuthService;
import com.placenextai.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {

    private final AuthService authService;
    private final StudentService studentService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody StudentRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerStudent(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request, "ROLE_STUDENT"));
    }

    @GetMapping("/profile")
    public ResponseEntity<StudentResponse> getProfile(Authentication authentication) {
        return ResponseEntity.ok(studentService.getProfile(authentication.getName()));
    }

    @PutMapping("/profile")
    public ResponseEntity<StudentResponse> updateProfile(
            Authentication authentication,
            @Valid @RequestBody StudentUpdateRequest request) {
        return ResponseEntity.ok(studentService.updateProfile(authentication.getName(), request));
    }
}
