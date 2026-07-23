package com.placenextai.service;

import com.placenextai.dto.DepartmentBreakdownResponse;
import com.placenextai.dto.FunnelResponse;
import com.placenextai.dto.SkillDistributionResponse;

import java.util.List;

public interface RecruiterAnalyticsService {

    List<FunnelResponse> funnel(String recruiterEmail);

    List<SkillDistributionResponse> skillDistribution(String recruiterEmail);

    List<DepartmentBreakdownResponse> departmentBreakdown(String recruiterEmail);
}
