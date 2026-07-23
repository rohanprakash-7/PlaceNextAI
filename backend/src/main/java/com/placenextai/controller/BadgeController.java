package com.placenextai.controller;

import com.placenextai.dto.BadgeResponse;
import com.placenextai.service.BadgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
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
}
