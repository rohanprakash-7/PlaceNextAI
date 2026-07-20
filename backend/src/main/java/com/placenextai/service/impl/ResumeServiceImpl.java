package com.placenextai.service.impl;

import com.placenextai.client.ResumeAiClient;
import com.placenextai.dto.AiResumeAnalysis;
import com.placenextai.dto.ResumeVersionResponse;
import com.placenextai.entity.EventType;
import com.placenextai.entity.ResumeVersion;
import com.placenextai.entity.Student;
import com.placenextai.exception.ResourceNotFoundException;
import com.placenextai.repository.ResumeVersionRepository;
import com.placenextai.repository.StudentRepository;
import com.placenextai.service.EventService;
import com.placenextai.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {

    private static final String SEPARATOR = "\u001F"; // unit separator: never appears in normal text

    private final ResumeVersionRepository resumeVersionRepository;
    private final StudentRepository studentRepository;
    private final ResumeAiClient resumeAiClient;
    private final EventService eventService;

    @Override
    @Transactional
    public ResumeVersionResponse uploadAndAnalyze(String studentEmail, MultipartFile file, String jobDescription) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please choose a PDF resume to upload.");
        }

        Student student = findStudent(studentEmail);
        AiResumeAnalysis analysis = resumeAiClient.analyze(file, jobDescription);

        int nextVersion = resumeVersionRepository
                .findTopByStudentIdOrderByVersionNumberDesc(student.getId())
                .map(latest -> latest.getVersionNumber() + 1)
                .orElse(1);

        ResumeVersion version = resumeVersionRepository.save(ResumeVersion.builder()
                .studentId(student.getId())
                .versionNumber(nextVersion)
                .fileName(file.getOriginalFilename() == null ? "resume.pdf" : file.getOriginalFilename())
                .atsScore(analysis.getAtsScore())
                .extractedSkills(join(analysis.getExtractedSkills()))
                .missingKeywords(join(analysis.getMissingKeywords()))
                .suggestions(join(analysis.getSuggestions()))
                .wordCount(analysis.getWordCount())
                .build());

        // Feed the readiness engine: resume dimension is now a REAL ATS score.
        student.setResumeScore(analysis.getAtsScore());
        student.setResumeUrl("internal://resume/v" + nextVersion);
        studentRepository.save(student);

        eventService.record(student.getId(), EventType.RESUME_UPLOADED,
                "Resume v" + nextVersion + " analyzed - ATS score " + analysis.getAtsScore());

        return toResponse(version);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResumeVersionResponse> getVersions(String studentEmail) {
        Student student = findStudent(studentEmail);
        return resumeVersionRepository.findByStudentIdOrderByVersionNumberDesc(student.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ResumeVersionResponse getVersion(String studentEmail, Long versionId) {
        Student student = findStudent(studentEmail);
        ResumeVersion version = resumeVersionRepository.findByIdAndStudentId(versionId, student.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Resume version not found: " + versionId));
        return toResponse(version);
    }

    private Student findStudent(String email) {
        return studentRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with email: " + email));
    }

    private String join(List<String> values) {
        return values == null ? "" : String.join(SEPARATOR, values);
    }

    private List<String> split(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return Arrays.stream(value.split(SEPARATOR)).filter(part -> !part.isBlank()).toList();
    }

    private ResumeVersionResponse toResponse(ResumeVersion version) {
        return ResumeVersionResponse.builder()
                .id(version.getId())
                .versionNumber(version.getVersionNumber())
                .fileName(version.getFileName())
                .atsScore(version.getAtsScore())
                .extractedSkills(split(version.getExtractedSkills()))
                .missingKeywords(split(version.getMissingKeywords()))
                .suggestions(split(version.getSuggestions()))
                .wordCount(version.getWordCount())
                .createdAt(version.getCreatedAt())
                .build();
    }
}
