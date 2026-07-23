package com.placenextai.controller;

import com.placenextai.dto.DayCountResponse;
import com.placenextai.dto.EventResponse;
import com.placenextai.dto.ReadinessResponse;
import com.placenextai.service.EventService;
import com.placenextai.service.ReadinessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student/readiness")
@RequiredArgsConstructor
public class ReadinessController {

    private final ReadinessService readinessService;
    private final EventService eventService;

    @GetMapping
    public ResponseEntity<ReadinessResponse> getReadiness(Authentication authentication) {
        return ResponseEntity.ok(readinessService.getReadiness(authentication.getName()));
    }

    @PostMapping("/recompute")
    public ResponseEntity<ReadinessResponse> recompute(Authentication authentication) {
        return ResponseEntity.ok(readinessService.recompute(authentication.getName(), "MANUAL_REFRESH"));
    }

    @GetMapping("/events")
    public ResponseEntity<List<EventResponse>> recentEvents(Authentication authentication) {
        return ResponseEntity.ok(eventService.recentEvents(authentication.getName()));
    }

    @GetMapping("/events/heatmap")
    public ResponseEntity<List<DayCountResponse>> activityHeatmap(
            Authentication authentication,
            @RequestParam(defaultValue = "90") int days) {
        return ResponseEntity.ok(eventService.activityHeatmap(authentication.getName(), days));
    }
}
