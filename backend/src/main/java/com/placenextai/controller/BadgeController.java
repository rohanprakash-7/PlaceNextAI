package com.placenextai.controller;

import com.placenextai.dto.BadgeResponse;
import com.placenextai.service.BadgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/student/badges")
@RequiredArgsConstructor
public class BadgeController {

    private final BadgeService badgeService;

    @GetMapping
    public ResponseEntity<List<BadgeResponse>> myBadges(Authentication authentication) {
        return ResponseEntity.ok(badgeService.getBadgesForStudent(authentication.getName()));
    }

    @GetMapping("/{code}/certificate")
    public ResponseEntity<byte[]> certificate(Authentication authentication, @PathVariable String code) {
        byte[] pdf = badgeService.generateCertificate(authentication.getName(), code);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header("Content-Disposition", "attachment; filename=" + code.toLowerCase() + "-certificate.pdf")
                .body(pdf);
    }
}
