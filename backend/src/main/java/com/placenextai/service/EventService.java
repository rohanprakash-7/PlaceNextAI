package com.placenextai.service;

import com.placenextai.dto.EventResponse;
import com.placenextai.entity.EventType;

import java.util.List;

public interface EventService {

    void record(Long studentId, EventType type, String payload);

    List<EventResponse> recentEvents(String studentEmail);
}
