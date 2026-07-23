package com.placenextai.service;

import com.placenextai.dto.CreateMentorSlotRequest;
import com.placenextai.dto.MentorBrowseResponse;
import com.placenextai.dto.MentorSlotResponse;

import java.util.List;

public interface MentorSlotService {

    MentorSlotResponse createSlot(String alumniEmail, CreateMentorSlotRequest request);

    List<MentorSlotResponse> getSlotsForAlumni(String alumniEmail);

    List<MentorSlotResponse> getSessionsForAlumni(String alumniEmail);

    List<MentorBrowseResponse> browseMentors();

    MentorSlotResponse bookSlot(String studentEmail, Long slotId);

    List<MentorSlotResponse> getSessionsForStudent(String studentEmail);

    String generateCalendarInvite(String studentEmail, Long slotId);
}
