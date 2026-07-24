package com.placenextai.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewQuestionResponse {

    private Long id;
    private int questionOrder;
    private String category;
    private String questionText;
    private boolean answered;
    private String studentAnswer;
    private Integer score;
    private String feedback;
}
