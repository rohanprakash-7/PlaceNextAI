package com.placenextai.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationResponse {

    private Long id;
    private Long jobId;
    private String jobTitle;
    private String company;
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private String status;
    private LocalDateTime appliedDate;
}
