package com.placenextai.service;

import com.placenextai.dto.AdminAnalyticsOverviewResponse;
import com.placenextai.dto.DepartmentAnalyticsResponse;
import com.placenextai.dto.RecruiterActivityResponse;
import com.placenextai.dto.RiskDistributionResponse;

import java.util.List;

public interface AdminAnalyticsService {

    AdminAnalyticsOverviewResponse overview();

    List<DepartmentAnalyticsResponse> departments();

    List<RecruiterActivityResponse> recruiterActivity();

    RiskDistributionResponse riskDistribution();
}
