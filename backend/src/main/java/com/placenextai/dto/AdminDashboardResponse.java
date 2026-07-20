package com.placenextai.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDashboardResponse {

    private long totalStudents;
    private long totalRecruiters;
    private long totalJobs;
    private long totalApplications;
}
