package com.placenextai.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorSlotResponse {

    private Long id;
    private Long alumniId;
    private String alumniName;
    private String alumniCompany;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean booked;
}
