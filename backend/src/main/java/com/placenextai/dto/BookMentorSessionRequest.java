package com.placenextai.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookMentorSessionRequest {

    @NotNull(message = "Slot id is required")
    private Long slotId;
}
