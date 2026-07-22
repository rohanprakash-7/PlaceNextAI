package com.placenextai.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentFeedbackSummaryResponse {

    private int totalFeedbackCount;
    private double avgCommunication;
    private double avgTechnical;
    private double avgProblemSolving;
    private double avgCultureFit;
    private int scoreAdjustment;
    private List<String> recentComments;
}
