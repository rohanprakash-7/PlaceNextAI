package com.placenextai.controller;

import com.placenextai.dto.BadgeResponse;
import com.placenextai.service.RecruiterBadgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recruiter/badges")
@RequiredArgsConstructor
public class RecruiterBadgeController {

    private final RecruiterBadgeService recruiterBadgeService;

    @GetMapping
    public ResponseEntity<List<BadgeResponse>> myBadges(Authentication authentication) {
        return ResponseEntity.ok(recruiterBadgeService.getBadgesForRecruiter(authentication.getName()));
    }
}
