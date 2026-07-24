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
    String SKILL_MASTER = "SKILL_MASTER";
    String INTERVIEW_ACE = "INTERVIEW_ACE";
    String PLACED = "PLACED";
    String MENTORSHIP_STARTER = "MENTORSHIP_STARTER";
    String STREAK_7 = "STREAK_7";
    String STREAK_30 = "STREAK_30";

    void checkAndAward(Long studentId, EventType triggeringType);

    void checkStreak(Long studentId, int currentStreak);

    List<BadgeResponse> getBadgesForStudent(String studentEmail);

    byte[] generateCertificate(String studentEmail, String badgeCode);
}
