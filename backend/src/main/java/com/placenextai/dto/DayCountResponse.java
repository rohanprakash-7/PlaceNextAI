package com.placenextai.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DayCountResponse {

    private LocalDate date;
    private long count;
}
