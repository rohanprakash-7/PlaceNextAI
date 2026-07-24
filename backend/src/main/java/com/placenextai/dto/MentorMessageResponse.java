package com.placenextai.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorMessageResponse {

    private Long id;
    private Long requestId;
    private String senderRole;
    private Long senderId;
    private String senderName;
    private String content;
    private LocalDateTime sentAt;
}
