package com.placenextai.service;

import com.placenextai.dto.CreateMentorSlotRequest;
import com.placenextai.dto.MentorBrowseResponse;
import com.placenextai.dto.MentorProfileResponse;
import com.placenextai.dto.MentorSlotResponse;

import java.util.List;

public interface MentorSlotService {

    MentorSlotResponse createSlot(String alumniEmail, CreateMentorSlotRequest request);

    List<MentorSlotResponse> getSlotsForAlumni(String alumniEmail);

    void deleteSlot(String alumniEmail, Long slotId);

    List<MentorSlotResponse> getSessionsForAlumni(String alumniEmail);

    List<MentorBrowseResponse> browseMentors(String studentEmail, String search, String company);

    List<String> listMentorCompanies();

    MentorProfileResponse getMentorProfile(String studentEmail, Long alumniId);

    MentorSlotResponse bookSlot(String studentEmail, Long slotId);

    List<MentorSlotResponse> getSessionsForStudent(String studentEmail);

    String generateCalendarInvite(String studentEmail, Long slotId);
}
