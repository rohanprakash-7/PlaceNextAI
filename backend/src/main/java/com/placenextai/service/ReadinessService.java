package com.placenextai.service;

import com.placenextai.dto.ReadinessResponse;
import com.placenextai.dto.ScoreConfigDto;

public interface ReadinessService {

    ReadinessResponse getReadiness(String studentEmail);

    ReadinessResponse recompute(String studentEmail, String triggeredBy);

    void recomputeForStudentId(Long studentId, String triggeredBy);

    ScoreConfigDto getConfig();

    ScoreConfigDto updateConfig(ScoreConfigDto request);
}
