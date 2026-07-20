package com.placenextai.service.impl;

import com.placenextai.dto.StudentResponse;
import com.placenextai.dto.StudentUpdateRequest;
import com.placenextai.entity.Student;
import com.placenextai.exception.ResourceNotFoundException;
import com.placenextai.repository.StudentRepository;
import com.placenextai.entity.EventType;
import com.placenextai.service.EventService;
import com.placenextai.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final EventService eventService;

    @Override
    @Transactional(readOnly = true)
    public StudentResponse getProfile(String email) {
        return toResponse(findByEmail(email));
    }

    @Override
    @Transactional
    public StudentResponse updateProfile(String email, StudentUpdateRequest request) {
        Student student = findByEmail(email);

        student.setFullName(request.getFullName());
        student.setPhone(request.getPhone());
        student.setCollege(request.getCollege());
        student.setBranch(request.getBranch());
        student.setGraduationYear(request.getGraduationYear());
        student.setCgpa(request.getCgpa());
        student.setSkills(request.getSkills());

        Student saved = studentRepository.save(student);
        eventService.record(saved.getId(), EventType.PROFILE_UPDATED, "Profile updated");
        return toResponse(saved);
    }

    private Student findByEmail(String email) {
        return studentRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with email: " + email));
    }

    private StudentResponse toResponse(Student student) {
        return StudentResponse.builder()
                .id(student.getId())
                .fullName(student.getFullName())
                .email(student.getEmail())
                .phone(student.getPhone())
                .college(student.getCollege())
                .branch(student.getBranch())
                .graduationYear(student.getGraduationYear())
                .cgpa(student.getCgpa())
                .skills(student.getSkills())
                .createdAt(student.getCreatedAt())
                .build();
    }
}
