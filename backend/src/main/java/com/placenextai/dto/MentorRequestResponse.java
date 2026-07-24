package com.placenextai.dto;

import com.placenextai.entity.MentorRequestStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorRequestResponse {

    private Long id;
    private Long studentId;
    private String studentName;
    private Long alumniId;
    private String alumniName;
    private String alumniCompany;
    private String topic;
    private String message;
    private MentorRequestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime respondedAt;
}
