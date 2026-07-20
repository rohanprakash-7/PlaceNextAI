package com.placenextai.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentResponse {

    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String college;
    private String branch;
    private Integer graduationYear;
    private Double cgpa;
    private String skills;
    private LocalDateTime createdAt;
}
