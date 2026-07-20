package com.placenextai.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventResponse {

    private Long id;
    private String eventType;
    private String payload;
    private LocalDateTime createdAt;
}
