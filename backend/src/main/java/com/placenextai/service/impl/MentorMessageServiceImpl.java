package com.placenextai.service.impl;

import com.placenextai.dto.MentorMessageRequest;
import com.placenextai.dto.MentorMessageResponse;
import com.placenextai.entity.Alumni;
import com.placenextai.entity.MentorMessage;
import com.placenextai.entity.MentorRequest;
import com.placenextai.entity.MentorRequestStatus;
import com.placenextai.entity.NotificationType;
import com.placenextai.entity.Student;
import com.placenextai.exception.ResourceNotFoundException;
import com.placenextai.repository.AlumniRepository;
import com.placenextai.repository.MentorMessageRepository;
import com.placenextai.repository.MentorRequestRepository;
import com.placenextai.repository.StudentRepository;
import com.placenextai.service.MentorMessageService;
import com.placenextai.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorMessageServiceImpl implements MentorMessageService {

    private final MentorMessageRepository mentorMessageRepository;
    private final MentorRequestRepository mentorRequestRepository;
    private final StudentRepository studentRepository;
    private final AlumniRepository alumniRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional(readOnly = true)
    public List<MentorMessageResponse> getMessages(String userEmail, Long requestId) {
        Participants participants = resolveParticipants(userEmail, requestId);
        return mentorMessageRepository.findByRequestIdOrderBySentAtAsc(requestId).stream()
                .map(message -> toResponse(message, participants))
                .toList();
    }

    @Override
    @Transactional
    public MentorMessageResponse sendMessage(String userEmail, Long requestId, MentorMessageRequest request) {
        Participants participants = resolveParticipants(userEmail, requestId);

        boolean isStudent = participants.student.getEmail().equalsIgnoreCase(userEmail);
        MentorMessage saved = mentorMessageRepository.save(MentorMessage.builder()
                .requestId(requestId)
                .senderRole(isStudent ? "STUDENT" : "ALUMNI")
                .senderId(isStudent ? participants.student.getId() : participants.alumni.getId())
                .content(request.getContent())
                .build());

        String senderName = isStudent ? participants.student.getFullName() : participants.alumni.getFullName();
        String preview = request.getContent().length() > 120
                ? request.getContent().substring(0, 117) + "..."
                : request.getContent();
        if (isStudent) {
            notificationService.notify(participants.alumni.getId(), "ROLE_ALUMNI", NotificationType.MENTOR_MESSAGE,
                    "New message from " + senderName, preview, "/dashboard/alumni/requests/" + requestId + "/messages");
        } else {
            notificationService.notify(participants.student.getId(), "ROLE_STUDENT", NotificationType.MENTOR_MESSAGE,
                    "New message from " + senderName, preview, "/dashboard/student/mentors/requests/" + requestId + "/messages");
        }

        return toResponse(saved, participants);
    }

    private Participants resolveParticipants(String userEmail, Long requestId) {
        MentorRequest mentorRequest = mentorRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor request not found: " + requestId));

        if (mentorRequest.getStatus() != MentorRequestStatus.ACCEPTED) {
            throw new IllegalArgumentException("Messaging is only available for accepted mentor requests");
        }

        Student student = studentRepository.findById(mentorRequest.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + mentorRequest.getStudentId()));
        Alumni alumni = alumniRepository.findById(mentorRequest.getAlumniId())
                .orElseThrow(() -> new ResourceNotFoundException("Alumni not found: " + mentorRequest.getAlumniId()));

        if (!student.getEmail().equalsIgnoreCase(userEmail) && !alumni.getEmail().equalsIgnoreCase(userEmail)) {
            throw new AccessDeniedException("You are not part of this mentor request");
        }

        return new Participants(student, alumni);
    }

    private MentorMessageResponse toResponse(MentorMessage message, Participants participants) {
        String senderName = "ALUMNI".equals(message.getSenderRole())
                ? participants.alumni.getFullName()
                : participants.student.getFullName();

        return MentorMessageResponse.builder()
                .id(message.getId())
                .requestId(message.getRequestId())
                .senderRole(message.getSenderRole())
                .senderId(message.getSenderId())
                .senderName(senderName)
                .content(message.getContent())
                .sentAt(message.getSentAt())
                .build();
    }

    private record Participants(Student student, Alumni alumni) {
    }
}
