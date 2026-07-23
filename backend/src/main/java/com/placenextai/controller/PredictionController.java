package com.placenextai.controller;

import com.placenextai.dto.PlacementPredictionResponse;
import com.placenextai.service.PlacementPredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student/prediction")
@RequiredArgsConstructor
public class PredictionController {

    private final PlacementPredictionService placementPredictionService;

    @GetMapping
    public ResponseEntity<PlacementPredictionResponse> getPrediction(Authentication authentication) {
        return ResponseEntity.ok(placementPredictionService.getPrediction(authentication.getName()));
    }

    @PostMapping("/recompute")
    public ResponseEntity<PlacementPredictionResponse> recompute(Authentication authentication) {
        return ResponseEntity.ok(placementPredictionService.recompute(authentication.getName()));
    }
}
