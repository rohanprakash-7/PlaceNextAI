package com.placenextai.service;

import com.placenextai.dto.MentorMessageRequest;
import com.placenextai.dto.MentorMessageResponse;

import java.util.List;

public interface MentorMessageService {

    List<MentorMessageResponse> getMessages(String userEmail, Long requestId);

    MentorMessageResponse sendMessage(String userEmail, Long requestId, MentorMessageRequest request);
}
