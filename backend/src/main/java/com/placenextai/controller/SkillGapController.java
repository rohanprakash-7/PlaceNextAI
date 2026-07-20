package com.placenextai.controller;

import com.placenextai.dto.SkillGapResponse;
import com.placenextai.service.SkillGapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student/skill-gap")
@RequiredArgsConstructor
public class SkillGapController {

    private final SkillGapService skillGapService;

    @GetMapping
    public ResponseEntity<SkillGapResponse> analyze(
            Authentication authentication,
            @RequestParam(required = false) String company) {
        return ResponseEntity.ok(skillGapService.analyze(authentication.getName(), company));
    }

    @GetMapping("/companies")
    public ResponseEntity<List<String>> companies() {
        return ResponseEntity.ok(skillGapService.listTargetCompanies());
    }
}
