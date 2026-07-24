package com.placenextai.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobResponse {

    private Long id;
    private String title;
    private String company;
    private String location;
    private String description;
    private String salary;
    private String skillsRequired;
    private Double minCgpa;
    private LocalDate createdDate;
}
