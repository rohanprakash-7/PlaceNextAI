package com.placenextai.service;

import com.placenextai.dto.BadgeResponse;
import com.placenextai.entity.EventType;

import java.util.List;

public interface BadgeService {

    String FIRST_APPLICATION = "FIRST_APPLICATION";
    String RESUME_REFINED = "RESUME_REFINED";
    String CONSISTENCY_STREAK = "CONSISTENCY_STREAK";
    String INTERVIEW_READY = "INTERVIEW_READY";
    String OFFER_RECEIVED = "OFFER_RECEIVED";

    void checkAndAward(Long studentId, EventType triggeringType);

    List<BadgeResponse> getBadgesForStudent(String studentEmail);
}
