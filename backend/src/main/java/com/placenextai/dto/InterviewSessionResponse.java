package com.placenextai.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewSessionResponse {

    private Long id;
    private String targetCompany;
    private String status;
    private Integer overallScore;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private List<InterviewQuestionResponse> questions;
}
