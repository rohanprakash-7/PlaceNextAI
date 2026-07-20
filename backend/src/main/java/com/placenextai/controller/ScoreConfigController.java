package com.placenextai.controller;

import com.placenextai.dto.ScoreConfigDto;
import com.placenextai.service.ReadinessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/score-config")
@RequiredArgsConstructor
public class ScoreConfigController {

    private final ReadinessService readinessService;

    @GetMapping
    public ResponseEntity<ScoreConfigDto> getConfig() {
        return ResponseEntity.ok(readinessService.getConfig());
    }

    @PutMapping
    public ResponseEntity<ScoreConfigDto> updateConfig(@Valid @RequestBody ScoreConfigDto request) {
        return ResponseEntity.ok(readinessService.updateConfig(request));
    }
}
