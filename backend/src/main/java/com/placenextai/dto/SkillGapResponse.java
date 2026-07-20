package com.placenextai.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillGapResponse {

    private String targetCompany;
    private List<String> currentSkills;
    private List<String> requiredSkills;
    private List<String> missingSkills;
    private int coveragePercent;
}
