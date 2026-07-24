package com.placenextai.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EligibleCompanySummary {

    private String company;
    private int matchPercent;
    private int successProbability;
    private String probabilityLabel;
}
