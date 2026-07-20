package com.placenextai.controller;

import com.placenextai.dto.*;
import com.placenextai.service.AuthService;
import com.placenextai.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recruiter")
@RequiredArgsConstructor
public class RecruiterController {

    private final AuthService authService;
    private final JobService jobService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RecruiterRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerRecruiter(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request, "ROLE_RECRUITER"));
    }

    @PostMapping("/jobs")
    public ResponseEntity<JobResponse> createJob(
            Authentication authentication,
            @Valid @RequestBody JobRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(jobService.createJob(request, authentication.getName()));
    }

    @GetMapping("/jobs")
    public ResponseEntity<List<JobResponse>> getMyJobs(Authentication authentication) {
        return ResponseEntity.ok(jobService.getJobsForRecruiter(authentication.getName()));
    }
}
