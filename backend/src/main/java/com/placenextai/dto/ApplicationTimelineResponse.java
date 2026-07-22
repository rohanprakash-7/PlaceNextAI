package com.placenextai.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationTimelineResponse {

    private Long applicationId;
    private String jobTitle;
    private String company;
    private String currentStatus;
    private boolean rejected;
    private List<ApplicationStageResponse> stages;
    private int feedbackCount;
}
