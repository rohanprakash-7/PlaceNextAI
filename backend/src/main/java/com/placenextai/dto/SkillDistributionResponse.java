package com.placenextai.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillDistributionResponse {

    private String skill;
    private long applicantCount;
    private long requiredCount;
}
