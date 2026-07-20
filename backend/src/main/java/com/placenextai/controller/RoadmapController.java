package com.placenextai.controller;

import com.placenextai.dto.GenerateRoadmapRequest;
import com.placenextai.dto.RoadmapResponse;
import com.placenextai.service.RoadmapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student/roadmap")
@RequiredArgsConstructor
public class RoadmapController {

    private final RoadmapService roadmapService;

    @PostMapping("/generate")
    public ResponseEntity<RoadmapResponse> generate(
            Authentication authentication,
            @RequestBody GenerateRoadmapRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(roadmapService.generate(authentication.getName(), request.getTargetCompany()));
    }

    @GetMapping
    public ResponseEntity<RoadmapResponse> getActive(Authentication authentication) {
        return ResponseEntity.ok(roadmapService.getActive(authentication.getName()));
    }

    @PostMapping("/items/{itemId}/complete")
    public ResponseEntity<RoadmapResponse> completeItem(
            Authentication authentication, @PathVariable Long itemId) {
        return ResponseEntity.ok(roadmapService.completeItem(authentication.getName(), itemId));
    }
}
