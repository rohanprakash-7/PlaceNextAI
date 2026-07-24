package com.placenextai.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HiringTrendResponse {

    private String month;
    private long applications;
    private long hires;
}
