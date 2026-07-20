package com.placenextai.service.impl;

import com.placenextai.dto.EventResponse;
import com.placenextai.entity.EventType;
import com.placenextai.entity.PlatformEvent;
import com.placenextai.entity.Student;
import com.placenextai.exception.ResourceNotFoundException;
import com.placenextai.repository.PlatformEventRepository;
import com.placenextai.repository.StudentRepository;
import com.placenextai.service.EventService;
import com.placenextai.service.ReadinessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final PlatformEventRepository eventRepository;
    private final StudentRepository studentRepository;
    private final ReadinessService readinessService;

    @Override
    @Transactional
    public void record(Long studentId, EventType type, String payload) {
        eventRepository.save(PlatformEvent.builder()
                .studentId(studentId)
                .eventType(type)
                .payload(payload)
                .build());
        // Every event re-scores the student: this is the platform's core loop.
        readinessService.recomputeForStudentId(studentId, type.name());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> recentEvents(String studentEmail) {
        Student student = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with email: " + studentEmail));
        return eventRepository.findTop20ByStudentIdOrderByCreatedAtDesc(student.getId()).stream()
                .map(event -> EventResponse.builder()
                        .id(event.getId())
                        .eventType(event.getEventType().name())
                        .payload(event.getPayload())
                        .createdAt(event.getCreatedAt())
                        .build())
                .toList();
    }
}
