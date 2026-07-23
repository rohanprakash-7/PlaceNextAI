package com.placenextai.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruiterActivityResponse {

    private Long recruiterId;
    private String companyName;
    private String recruiterName;
    private long feedbackCount;
    private long applicationsReceived;
}
