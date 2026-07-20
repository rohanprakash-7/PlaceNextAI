package com.placenextai.service.impl;

import com.placenextai.dto.RoadmapItemResponse;
import com.placenextai.dto.RoadmapResponse;
import com.placenextai.dto.SkillGapResponse;
import com.placenextai.entity.*;
import com.placenextai.exception.ResourceNotFoundException;
import com.placenextai.repository.RoadmapItemRepository;
import com.placenextai.repository.RoadmapRepository;
import com.placenextai.repository.StudentRepository;
import com.placenextai.service.EventService;
import com.placenextai.service.RoadmapService;
import com.placenextai.service.SkillGapService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoadmapServiceImpl implements RoadmapService {

    private static final int SKILLS_PER_WEEK = 2;

    private final RoadmapRepository roadmapRepository;
    private final RoadmapItemRepository roadmapItemRepository;
    private final StudentRepository studentRepository;
    private final SkillGapService skillGapService;
    private final EventService eventService;

    @Override
    @Transactional
    public RoadmapResponse generate(String studentEmail, String targetCompany) {
        Student student = findStudent(studentEmail);
        SkillGapResponse gap = skillGapService.analyze(studentEmail, targetCompany);

        // Archive any existing active roadmap - history is kept, not deleted.
        roadmapRepository.findTopByStudentIdAndStatusOrderByCreatedAtDesc(student.getId(), RoadmapStatus.ACTIVE)
                .ifPresent(existing -> {
                    existing.setStatus(RoadmapStatus.ARCHIVED);
                    roadmapRepository.save(existing);
                });

        Roadmap roadmap = roadmapRepository.save(Roadmap.builder()
                .studentId(student.getId())
                .targetCompany(gap.getTargetCompany())
                .build());

        List<String> missing = gap.getMissingSkills();
        int week = 1;
        for (int i = 0; i < missing.size(); i += SKILLS_PER_WEEK) {
            List<String> chunk = missing.subList(i, Math.min(i + SKILLS_PER_WEEK, missing.size()));
            roadmapItemRepository.save(RoadmapItem.builder()
                    .roadmapId(roadmap.getId())
                    .weekNumber(week)
                    .title("Week " + week + ": " + String.join(" & ", chunk))
                    .skillTag(String.join(",", chunk))
                    .completed(false)
                    .build());
            week++;
        }

        roadmapItemRepository.save(RoadmapItem.builder()
                .roadmapId(roadmap.getId())
                .weekNumber(week)
                .title("Week " + week + ": Mock interview & HR round preparation")
                .skillTag("Interview")
                .completed(false)
                .build());

        return toResponse(roadmap);
    }

    @Override
    @Transactional(readOnly = true)
    public RoadmapResponse getActive(String studentEmail) {
        Student student = findStudent(studentEmail);
        Roadmap roadmap = roadmapRepository
                .findTopByStudentIdAndStatusOrderByCreatedAtDesc(student.getId(), RoadmapStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("No roadmap yet - generate one first."));
        return toResponse(roadmap);
    }

    @Override
    @Transactional
    public RoadmapResponse completeItem(String studentEmail, Long itemId) {
        Student student = findStudent(studentEmail);
        Roadmap roadmap = roadmapRepository
                .findTopByStudentIdAndStatusOrderByCreatedAtDesc(student.getId(), RoadmapStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("No active roadmap found."));

        RoadmapItem item = roadmapItemRepository.findByIdAndRoadmapId(itemId, roadmap.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Roadmap item not found: " + itemId));

        if (!item.isCompleted()) {
            item.setCompleted(true);
            item.setCompletedAt(LocalDateTime.now());
            roadmapItemRepository.save(item);
            eventService.record(student.getId(), EventType.ROADMAP_ITEM_COMPLETED, item.getTitle());
        }

        return toResponse(roadmap);
    }

    private Student findStudent(String email) {
        return studentRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with email: " + email));
    }

    private RoadmapResponse toResponse(Roadmap roadmap) {
        List<RoadmapItem> items = roadmapItemRepository.findByRoadmapIdOrderByWeekNumberAsc(roadmap.getId());
        long completed = items.stream().filter(RoadmapItem::isCompleted).count();
        int progress = items.isEmpty() ? 0 : (int) Math.round(100.0 * completed / items.size());

        return RoadmapResponse.builder()
                .id(roadmap.getId())
                .targetCompany(roadmap.getTargetCompany())
                .totalItems(items.size())
                .completedItems((int) completed)
                .progressPercent(progress)
                .createdAt(roadmap.getCreatedAt())
                .items(items.stream()
                        .map(item -> RoadmapItemResponse.builder()
                                .id(item.getId())
                                .weekNumber(item.getWeekNumber())
                                .title(item.getTitle())
                                .skillTag(item.getSkillTag())
                                .completed(item.isCompleted())
                                .completedAt(item.getCompletedAt())
                                .build())
                        .toList())
                .build();
    }
}
