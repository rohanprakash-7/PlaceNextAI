package com.placenextai.service.impl;

import com.placenextai.dto.MentorReviewRequest;
import com.placenextai.dto.MentorReviewResponse;
import com.placenextai.entity.EventType;
import com.placenextai.entity.MentorReview;
import com.placenextai.entity.MentorSlot;
import com.placenextai.entity.Student;
import com.placenextai.exception.DuplicateResourceException;
import com.placenextai.exception.ResourceNotFoundException;
import com.placenextai.repository.MentorReviewRepository;
import com.placenextai.repository.MentorSlotRepository;
import com.placenextai.repository.StudentRepository;
import com.placenextai.service.EventService;
import com.placenextai.service.MentorReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorReviewServiceImpl implements MentorReviewService {

    private final MentorReviewRepository mentorReviewRepository;
    private final MentorSlotRepository mentorSlotRepository;
    private final StudentRepository studentRepository;
    private final EventService eventService;

    @Override
    @Transactional
    public MentorReviewResponse submitReview(String studentEmail, MentorReviewRequest request) {
        Student student = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with email: " + studentEmail));
        MentorSlot slot = mentorSlotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new ResourceNotFoundException("Mentor slot not found: " + request.getSlotId()));

        if (slot.getStudentId() == null || !slot.getStudentId().equals(student.getId())) {
            throw new AccessDeniedException("You can only review sessions you booked");
        }
        if (slot.getEndTime().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("You can only review a session after it has ended");
        }
        if (mentorReviewRepository.existsBySlotId(slot.getId())) {
            throw new DuplicateResourceException("You have already reviewed this session");
        }

        MentorReview saved = mentorReviewRepository.save(MentorReview.builder()
                .slotId(slot.getId())
                .alumniId(slot.getAlumniId())
                .studentId(student.getId())
                .rating(request.getRating())
                .comment(request.getComment())
                .build());

        eventService.record(student.getId(), EventType.MENTOR_REVIEW_SUBMITTED,
                "Rated a mentor session " + request.getRating() + "/5");

        return toResponse(saved, student);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MentorReviewResponse> getReviewsForAlumni(Long alumniId) {
        return mentorReviewRepository.findByAlumniIdOrderByCreatedAtDesc(alumniId).stream()
                .map(review -> toResponse(review, studentRepository.findById(review.getStudentId()).orElse(null)))
                .toList();
    }

    private MentorReviewResponse toResponse(MentorReview review, Student student) {
        return MentorReviewResponse.builder()
                .id(review.getId())
                .alumniId(review.getAlumniId())
                .studentId(review.getStudentId())
                .studentName(student == null ? "Student" : student.getFullName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
