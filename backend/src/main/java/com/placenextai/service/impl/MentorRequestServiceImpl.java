package com.placenextai.service.impl;

import com.placenextai.dto.MentorRequestCreateRequest;
import com.placenextai.dto.MentorRequestResponse;
import com.placenextai.entity.Alumni;
import com.placenextai.entity.EventType;
import com.placenextai.entity.MentorRequest;
import com.placenextai.entity.MentorRequestStatus;
import com.placenextai.entity.NotificationType;
import com.placenextai.entity.Student;
import com.placenextai.exception.ResourceNotFoundException;
import com.placenextai.repository.AlumniRepository;
import com.placenextai.repository.MentorRequestRepository;
import com.placenextai.repository.StudentRepository;
import com.placenextai.service.EmailService;
import com.placenextai.service.EventService;
import com.placenextai.service.MentorRequestService;
import com.placenextai.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorRequestServiceImpl implements MentorRequestService {

    private final MentorRequestRepository mentorRequestRepository;
    private final StudentRepository studentRepository;
    private final AlumniRepository alumniRepository;
    private final EventService eventService;
    private final NotificationService notificationService;
    private final EmailService emailService;

    @Override
    @Transactional
    public MentorRequestResponse sendRequest(String studentEmail, MentorRequestCreateRequest request) {
        Student student = findStudent(studentEmail);
        Alumni alumni = findAlumniById(request.getAlumniId());

        MentorRequest saved = mentorRequestRepository.save(MentorRequest.builder()
                .studentId(student.getId())
                .alumniId(alumni.getId())
                .topic(request.getTopic())
                .message(request.getMessage())
                .status(MentorRequestStatus.PENDING)
                .build());

        eventService.record(student.getId(), EventType.MENTOR_REQUEST_SENT,
                "Requested mentorship from " + alumni.getFullName() + " (" + request.getTopic() + ")");

        String message = student.getFullName() + " requested mentorship on \"" + request.getTopic() + "\"";
        notificationService.notify(alumni.getId(), "ROLE_ALUMNI", NotificationType.MENTOR_REQUEST,
                "New mentorship request", message, "/dashboard/alumni/requests");
        emailService.send(alumni.getEmail(), "New mentorship request", message);

        return toResponse(saved, student, alumni);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MentorRequestResponse> getRequestsForStudent(String studentEmail) {
        Student student = findStudent(studentEmail);
        return mentorRequestRepository.findByStudentIdOrderByCreatedAtDesc(student.getId()).stream()
                .map(mentorRequest -> toResponse(mentorRequest, student, findAlumniById(mentorRequest.getAlumniId())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MentorRequestResponse> getRequestsForAlumni(String alumniEmail) {
        Alumni alumni = findAlumni(alumniEmail);
        return mentorRequestRepository.findByAlumniIdOrderByCreatedAtDesc(alumni.getId()).stream()
                .map(mentorRequest -> toResponse(mentorRequest, findStudentById(mentorRequest.getStudentId()), alumni))
                .toList();
    }

    @Override
    @Transactional
    public MentorRequestResponse accept(String alumniEmail, Long requestId) {
        return respond(alumniEmail, requestId, MentorRequestStatus.ACCEPTED, EventType.MENTOR_REQUEST_ACCEPTED);
    }

    @Override
    @Transactional
    public MentorRequestResponse reject(String alumniEmail, Long requestId) {
        return respond(alumniEmail, requestId, MentorRequestStatus.REJECTED, EventType.MENTOR_REQUEST_REJECTED);
    }

    @Override
    @Transactional(readOnly = true)
    public MentorRequestResponse getForParticipant(String userEmail, Long requestId) {
        MentorRequest mentorRequest = findRequest(requestId);
        Student student = findStudentById(mentorRequest.getStudentId());
        Alumni alumni = findAlumniById(mentorRequest.getAlumniId());

        if (!student.getEmail().equalsIgnoreCase(userEmail) && !alumni.getEmail().equalsIgnoreCase(userEmail)) {
            throw new AccessDeniedException("You are not part of this mentor request");
        }

        return toResponse(mentorRequest, student, alumni);
    }

    private MentorRequestResponse respond(String alumniEmail, Long requestId, MentorRequestStatus status, EventType eventType) {
        Alumni alumni = findAlumni(alumniEmail);
        MentorRequest mentorRequest = findRequest(requestId);

        if (!mentorRequest.getAlumniId().equals(alumni.getId())) {
            throw new AccessDeniedException("You do not have permission to respond to this request");
        }
        if (mentorRequest.getStatus() != MentorRequestStatus.PENDING) {
            throw new IllegalArgumentException("This request has already been responded to");
        }

        mentorRequest.setStatus(status);
        mentorRequest.setRespondedAt(java.time.LocalDateTime.now());
        MentorRequest saved = mentorRequestRepository.save(mentorRequest);

        Student student = findStudentById(saved.getStudentId());
        eventService.record(student.getId(), eventType,
                alumni.getFullName() + " " + status.name().toLowerCase() + " your mentorship request");

        return toResponse(saved, student, alumni);
    }

    private MentorRequestResponse toResponse(MentorRequest mentorRequest, Student student, Alumni alumni) {
        return MentorRequestResponse.builder()
                .id(mentorRequest.getId())
                .studentId(student.getId())
                .studentName(student.getFullName())
                .alumniId(alumni.getId())
                .alumniName(alumni.getFullName())
                .alumniCompany(alumni.getCurrentCompany())
                .topic(mentorRequest.getTopic())
                .message(mentorRequest.getMessage())
                .status(mentorRequest.getStatus())
                .createdAt(mentorRequest.getCreatedAt())
                .respondedAt(mentorRequest.getRespondedAt())
                .build();
    }

    private MentorRequest findRequest(Long id) {
        return mentorRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor request not found: " + id));
    }

    private Student findStudent(String email) {
        return studentRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with email: " + email));
    }

    private Student findStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + id));
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
