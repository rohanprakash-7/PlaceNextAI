package com.placenextai.controller;

import com.placenextai.dto.ResumeVersionResponse;
import com.placenextai.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/student/resume")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResumeVersionResponse> upload(
            Authentication authentication,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "jobDescription", required = false) String jobDescription) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resumeService.uploadAndAnalyze(authentication.getName(), file, jobDescription));
    }

    @GetMapping("/versions")
    public ResponseEntity<List<ResumeVersionResponse>> versions(Authentication authentication) {
        return ResponseEntity.ok(resumeService.getVersions(authentication.getName()));
    }

    @GetMapping("/versions/{id}")
    public ResponseEntity<ResumeVersionResponse> version(
            Authentication authentication, @PathVariable Long id) {
        return ResponseEntity.ok(resumeService.getVersion(authentication.getName(), id));
    }
}
