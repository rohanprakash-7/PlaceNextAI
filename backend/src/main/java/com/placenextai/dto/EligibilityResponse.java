package com.placenextai.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EligibilityResponse {

    private Long jobId;
    private String jobTitle;
    private String company;
    private List<String> matchedSkills;
    private List<String> missingSkills;
    private int matchPercent;
    private boolean skillsEligible;
    private Double requiredCgpa;
    private Double studentCgpa;
    private boolean cgpaEligible;
    private boolean overallEligible;
    private int successProbability;
    private String probabilityLabel;
}
