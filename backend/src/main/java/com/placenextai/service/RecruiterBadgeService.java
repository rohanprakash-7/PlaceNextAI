package com.placenextai.service;

import com.placenextai.dto.BadgeResponse;

import java.util.List;

public interface RecruiterBadgeService {

    String JOB_POSTER = "JOB_POSTER";
    String ACTIVE_RECRUITER = "ACTIVE_RECRUITER";
    String TOP_HIRER = "TOP_HIRER";

    void checkAndAward(Long recruiterId);

    List<BadgeResponse> getBadgesForRecruiter(String recruiterEmail);
}
