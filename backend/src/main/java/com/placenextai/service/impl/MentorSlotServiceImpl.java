package com.placenextai.service.impl;

import com.placenextai.dto.CreateMentorSlotRequest;
import com.placenextai.dto.MentorBrowseResponse;
import com.placenextai.dto.MentorSlotResponse;
import com.placenextai.entity.Alumni;
import com.placenextai.entity.EventType;
import com.placenextai.entity.MentorSlot;
import com.placenextai.entity.Student;
import com.placenextai.exception.DuplicateResourceException;
import com.placenextai.exception.ResourceNotFoundException;
import com.placenextai.repository.AlumniRepository;
import com.placenextai.repository.MentorSlotRepository;
import com.placenextai.repository.StudentRepository;
import com.placenextai.service.EventService;
import com.placenextai.service.MentorSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MentorSlotServiceImpl implements MentorSlotService {

    private static final DateTimeFormatter ICS_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");

    private final AlumniRepository alumniRepository;
    private final MentorSlotRepository mentorSlotRepository;
    private final StudentRepository studentRepository;
    private final EventService eventService;

    @Override
    @Transactional
    public MentorSlotResponse createSlot(String alumniEmail, CreateMentorSlotRequest request) {
        Alumni alumni = findAlumni(alumniEmail);
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        MentorSlot slot = mentorSlotRepository.save(MentorSlot.builder()
                .alumniId(alumni.getId())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .booked(false)
                .build());

        return toResponse(slot, alumni);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MentorSlotResponse> getSlotsForAlumni(String alumniEmail) {
        Alumni alumni = findAlumni(alumniEmail);
        return mentorSlotRepository.findByAlumniIdOrderByStartTimeAsc(alumni.getId()).stream()
                .map(slot -> toResponse(slot, alumni))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MentorSlotResponse> getSessionsForAlumni(String alumniEmail) {
        Alumni alumni = findAlumni(alumniEmail);
        return mentorSlotRepository.findByAlumniIdAndBookedTrueOrderByStartTimeAsc(alumni.getId()).stream()
                .map(slot -> toResponse(slot, alumni))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MentorBrowseResponse> browseMentors() {
        List<MentorSlot> openSlots = mentorSlotRepository
                .findByBookedFalseAndStartTimeAfterOrderByStartTimeAsc(LocalDateTime.now());
        Map<Long, List<MentorSlot>> byAlumni = openSlots.stream()
                .collect(Collectors.groupingBy(MentorSlot::getAlumniId));

        return alumniRepository.findAll().stream()
                .map(alumni -> MentorBrowseResponse.builder()
                        .alumniId(alumni.getId())
                        .fullName(alumni.getFullName())
                        .currentCompany(alumni.getCurrentCompany())
                        .designation(alumni.getDesignation())
                        .expertise(alumni.getExpertise())
                        .bio(alumni.getBio())
                        .openSlots(byAlumni.getOrDefault(alumni.getId(), List.of()).stream()
                                .map(slot -> toResponse(slot, alumni))
                                .toList())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public MentorSlotResponse bookSlot(String studentEmail, Long slotId) {
        Student student = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with email: " + studentEmail));
        MentorSlot slot = mentorSlotRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor slot not found: " + slotId));

        if (slot.isBooked()) {
            throw new DuplicateResourceException("This slot has already been booked");
        }

        slot.setBooked(true);
        slot.setStudentId(student.getId());
        MentorSlot saved = mentorSlotRepository.save(slot);

        Alumni alumni = findAlumniById(slot.getAlumniId());
        eventService.record(student.getId(), EventType.MENTOR_SESSION_BOOKED,
                "Booked a mentor session with " + alumni.getFullName());

        return toResponse(saved, alumni);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MentorSlotResponse> getSessionsForStudent(String studentEmail) {
        Student student = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with email: " + studentEmail));
        return mentorSlotRepository.findByStudentIdOrderByStartTimeAsc(student.getId()).stream()
                .map(slot -> toResponse(slot, findAlumniById(slot.getAlumniId())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public String generateCalendarInvite(String studentEmail, Long slotId) {
        Student student = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with email: " + studentEmail));
        MentorSlot slot = mentorSlotRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor slot not found: " + slotId));

        if (slot.getStudentId() == null || !slot.getStudentId().equals(student.getId())) {
            throw new AccessDeniedException("You do not have permission to download this invite");
        }

        Alumni alumni = findAlumniById(slot.getAlumniId());

        return "BEGIN:VCALENDAR\r\n"
                + "VERSION:2.0\r\n"
                + "PRODID:-//PlaceNextAI//Mentor Sessions//EN\r\n"
                + "BEGIN:VEVENT\r\n"
                + "UID:mentor-session-" + slot.getId() + "@placenextai\r\n"
                + "DTSTART:" + slot.getStartTime().format(ICS_FORMAT) + "\r\n"
                + "DTEND:" + slot.getEndTime().format(ICS_FORMAT) + "\r\n"
                + "SUMMARY:Mentor session with " + alumni.getFullName() + "\r\n"
                + "DESCRIPTION:PlaceNextAI mentor session with " + alumni.getFullName()
                + " (" + (alumni.getCurrentCompany() == null ? "" : alumni.getCurrentCompany()) + ")\r\n"
                + "END:VEVENT\r\n"
                + "END:VCALENDAR\r\n";
    }

    private MentorSlotResponse toResponse(MentorSlot slot, Alumni alumni) {
        return MentorSlotResponse.builder()
                .id(slot.getId())
                .alumniId(slot.getAlumniId())
                .alumniName(alumni.getFullName())
                .alumniCompany(alumni.getCurrentCompany())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .booked(slot.isBooked())
                .build();
    }

    private Alumni findAlumni(String email) {
        return alumniRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Alumni not found with email: " + email));
    }

    private Alumni findAlumniById(Long id) {
        return alumniRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alumni not found: " + id));
    }
}
