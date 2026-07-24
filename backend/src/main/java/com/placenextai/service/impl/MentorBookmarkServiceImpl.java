package com.placenextai.service.impl;

import com.placenextai.dto.MentorBookmarkResponse;
import com.placenextai.entity.Alumni;
import com.placenextai.entity.MentorBookmark;
import com.placenextai.entity.Student;
import com.placenextai.exception.ResourceNotFoundException;
import com.placenextai.repository.AlumniRepository;
import com.placenextai.repository.MentorBookmarkRepository;
import com.placenextai.repository.MentorReviewRepository;
import com.placenextai.repository.StudentRepository;
import com.placenextai.service.MentorBookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MentorBookmarkServiceImpl implements MentorBookmarkService {

    private final MentorBookmarkRepository mentorBookmarkRepository;
    private final StudentRepository studentRepository;
    private final AlumniRepository alumniRepository;
    private final MentorReviewRepository mentorReviewRepository;

    @Override
    @Transactional
    public boolean toggleBookmark(String studentEmail, Long alumniId) {
        Student student = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with email: " + studentEmail));
        alumniRepository.findById(alumniId)
                .orElseThrow(() -> new ResourceNotFoundException("Alumni not found: " + alumniId));

        Optional<MentorBookmark> existing = mentorBookmarkRepository.findByStudentIdAndAlumniId(student.getId(), alumniId);
        if (existing.isPresent()) {
            mentorBookmarkRepository.delete(existing.get());
            return false;
        }

        mentorBookmarkRepository.save(MentorBookmark.builder()
                .studentId(student.getId())
                .alumniId(alumniId)
                .build());
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MentorBookmarkResponse> getBookmarks(String studentEmail) {
        Student student = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with email: " + studentEmail));

        return mentorBookmarkRepository.findByStudentIdOrderByCreatedAtDesc(student.getId()).stream()
                .map(bookmark -> {
                    Alumni alumni = alumniRepository.findById(bookmark.getAlumniId()).orElse(null);
                    return MentorBookmarkResponse.builder()
                            .alumniId(bookmark.getAlumniId())
                            .fullName(alumni == null ? "Unknown" : alumni.getFullName())
                            .currentCompany(alumni == null ? null : alumni.getCurrentCompany())
                            .designation(alumni == null ? null : alumni.getDesignation())
                            .averageRating(mentorReviewRepository.averageRatingForAlumni(bookmark.getAlumniId()))
                            .bookmarkedAt(bookmark.getCreatedAt())
                            .build();
                })
                .toList();
    }
}
