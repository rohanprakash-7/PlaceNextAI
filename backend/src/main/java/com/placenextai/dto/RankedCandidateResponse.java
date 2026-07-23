package com.placenextai.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RankedCandidateResponse {

    private Long studentId;
    private String studentName;
    private String email;
    private Long jobId;
    private String jobTitle;
    private double rankScore;
    private int skillMatchPercent;
    private int readinessScore;
    private int predictionScore;
    private int interviewSignal;
    private List<String> matchedSkills;
    private List<String> missingSkills;
}
