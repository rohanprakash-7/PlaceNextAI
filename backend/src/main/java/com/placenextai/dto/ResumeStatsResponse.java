package com.placenextai.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeStatsResponse {

    private long totalResumeVersions;
    private long studentsWithResume;
    private double averageAtsScore;
    private double averageVersionsPerStudent;
}
