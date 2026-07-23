package com.placenextai.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BadgeResponse {

    private String code;
    private String name;
    private String description;
    private String icon;
    private boolean earned;
    private LocalDateTime awardedAt;
}
