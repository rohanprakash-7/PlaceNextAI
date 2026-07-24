package com.placenextai.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillAnalyticsResponse {

    private List<SkillCountResponse> topStudentSkills;
    private List<SkillCountResponse> topDemandedSkills;
}
