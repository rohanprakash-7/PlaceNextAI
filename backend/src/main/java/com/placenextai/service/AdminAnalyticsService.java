package com.placenextai.service;

import com.placenextai.dto.AdminAnalyticsOverviewResponse;
import com.placenextai.dto.AiPredictionAnalyticsResponse;
import com.placenextai.dto.CollegeAnalyticsResponse;
import com.placenextai.dto.DayCountResponse;
import com.placenextai.dto.DepartmentAnalyticsResponse;
import com.placenextai.dto.HiringTrendResponse;
import com.placenextai.dto.InterviewStatsResponse;
import com.placenextai.dto.RecruiterActivityResponse;
import com.placenextai.dto.ResumeStatsResponse;
import com.placenextai.dto.RiskDistributionResponse;
import com.placenextai.dto.SkillAnalyticsResponse;
import com.placenextai.dto.StudentAnalyticsResponse;

import java.util.List;

public interface AdminAnalyticsService {

    AdminAnalyticsOverviewResponse overview();

    List<DepartmentAnalyticsResponse> departments();

    List<RecruiterActivityResponse> recruiterActivity();

    RiskDistributionResponse riskDistribution();

    List<CollegeAnalyticsResponse> colleges();

    StudentAnalyticsResponse studentAnalytics();

    List<HiringTrendResponse> hiringTrends();

    ResumeStatsResponse resumeStatistics();

    InterviewStatsResponse interviewStatistics();

    SkillAnalyticsResponse skillAnalytics();

    AiPredictionAnalyticsResponse aiPredictionAnalytics();

    List<DayCountResponse> platformHeatmap(int days);
}
