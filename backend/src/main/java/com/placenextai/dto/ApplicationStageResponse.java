package com.placenextai.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationStageResponse {

    private String status;
    private String label;
    private boolean reached;
    private boolean current;
}
