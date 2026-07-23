package com.placenextai.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentAnalyticsResponse {

    private String branch;
    private long studentCount;
    private double averageReadiness;
}
