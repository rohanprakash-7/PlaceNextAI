package com.placenextai.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeVersionResponse {

    private Long id;
    private int versionNumber;
    private String fileName;
    private int atsScore;
    private List<String> extractedSkills;
    private List<String> missingKeywords;
    private List<String> suggestions;
    private Integer wordCount;
    private LocalDateTime createdAt;
    private List<EligibleCompanySummary> eligibleCompanies;
}
