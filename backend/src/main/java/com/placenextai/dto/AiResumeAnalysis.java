package com.placenextai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiResumeAnalysis {

    @JsonProperty("ats_score")
    private int atsScore;

    @JsonProperty("extracted_skills")
    private List<String> extractedSkills;

    @JsonProperty("missing_keywords")
    private List<String> missingKeywords;

    private List<String> suggestions;

    @JsonProperty("word_count")
    private Integer wordCount;
}
