package com.placenextai.controller;

import com.placenextai.dto.ApplicationRequest;
import com.placenextai.dto.ApplicationResponse;
import com.placenextai.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/application")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/apply")
    public ResponseEntity<ApplicationResponse> apply(
            Authentication authentication,
            @Valid @RequestBody ApplicationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(applicationService.apply(authentication.getName(), request));
    }

    @GetMapping("/student")
    public ResponseEntity<List<ApplicationResponse>> getStudentApplications(Authentication authentication) {
        return ResponseEntity.ok(applicationService.getApplicationsForStudent(authentication.getName()));
    }

    @GetMapping("/recruiter")
    public ResponseEntity<List<ApplicationResponse>> getRecruiterApplications(Authentication authentication) {
        return ResponseEntity.ok(applicationService.getApplicationsForRecruiter(authentication.getName()));
    }
}
