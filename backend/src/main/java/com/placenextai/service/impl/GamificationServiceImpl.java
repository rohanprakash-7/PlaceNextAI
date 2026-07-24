package com.placenextai.service.impl;

import com.placenextai.dto.GamificationSummaryResponse;
import com.placenextai.dto.LeaderboardEntryResponse;
import com.placenextai.dto.LeaderboardResponse;
import com.placenextai.entity.EventType;
import com.placenextai.entity.Student;
import com.placenextai.exception.ResourceNotFoundException;
import com.placenextai.repository.StudentRepository;
import com.placenextai.service.GamificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GamificationServiceImpl implements GamificationService {

    private static final int XP_PER_LEVEL = 100;

    private static final Map<EventType, Integer> XP_BY_EVENT = new EnumMap<>(EventType.class);

    static {
        XP_BY_EVENT.put(EventType.LOGIN, 5);
        XP_BY_EVENT.put(EventType.PROFILE_UPDATED, 5);
        XP_BY_EVENT.put(EventType.RESUME_UPLOADED, 15);
        XP_BY_EVENT.put(EventType.APPLICATION_SUBMITTED, 10);
        XP_BY_EVENT.put(EventType.APPLICATION_STATUS_CHANGED, 5);
        XP_BY_EVENT.put(EventType.MOCK_INTERVIEW_COMPLETED, 25);
        XP_BY_EVENT.put(EventType.ROADMAP_ITEM_COMPLETED, 10);
        XP_BY_EVENT.put(EventType.FEEDBACK_RECEIVED, 5);
        XP_BY_EVENT.put(EventType.MENTOR_SESSION_BOOKED, 15);
        XP_BY_EVENT.put(EventType.MENTOR_REQUEST_SENT, 5);
        XP_BY_EVENT.put(EventType.MENTOR_REQUEST_ACCEPTED, 10);
        XP_BY_EVENT.put(EventType.MENTOR_REQUEST_REJECTED, 0);
        XP_BY_EVENT.put(EventType.MENTOR_REVIEW_SUBMITTED, 10);
    }

    private final StudentRepository studentRepository;

    @Override
    @Transactional
    public int recordActivity(Long studentId, EventType type) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));

        updateStreak(student);
        int xpGain = XP_BY_EVENT.getOrDefault(type, 0);
        if (xpGain > 0) {
            student.setXp(student.getXp() + xpGain);
        }
        studentRepository.save(student);
        return student.getCurrentStreak();
    }

    @Override
    @Transactional
    public void addXp(Long studentId, int amount) {
        if (amount == 0) {
            return;
        }
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));
        student.setXp(student.getXp() + amount);
        studentRepository.save(student);
    }

    @Override
    @Transactional(readOnly = true)
    public GamificationSummaryResponse getSummary(String studentEmail) {
        Student student = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with email: " + studentEmail));
        return toSummary(student);
    }

    @Override
    @Transactional(readOnly = true)
    public LeaderboardResponse getLeaderboard(String studentEmail) {
        Student me = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with email: " + studentEmail));

        List<Student> top = studentRepository.findTop20ByOrderByXpDesc();
        List<LeaderboardEntryResponse> entries = new java.util.ArrayList<>();
        int rank = 1;
        for (Student student : top) {
            entries.add(toEntry(student, rank++, student.getId().equals(me.getId())));
        }

        int myRank = (int) studentRepository.countByXpGreaterThan(me.getXp()) + 1;
        LeaderboardEntryResponse myEntry = toEntry(me, myRank, true);

        return LeaderboardResponse.builder().topEntries(entries).myEntry(myEntry).build();
    }

    private void updateStreak(Student student) {
        LocalDate today = LocalDate.now();
        LocalDate last = student.getLastActivityDate();

        if (last != null && last.equals(today)) {
            // Activity already recorded today - streak unchanged.
            return;
        }

        if (last != null && last.equals(today.minusDays(1))) {
            student.setCurrentStreak(student.getCurrentStreak() + 1);
        } else {
            student.setCurrentStreak(1);
        }

        student.setLongestStreak(Math.max(student.getLongestStreak(), student.getCurrentStreak()));
        student.setLastActivityDate(today);
    }

    private LeaderboardEntryResponse toEntry(Student student, int rank, boolean currentUser) {
        return LeaderboardEntryResponse.builder()
                .rank(rank)
                .studentId(student.getId())
                .fullName(student.getFullName())
                .college(student.getCollege())
                .branch(student.getBranch())
                .xp(student.getXp())
                .level(levelFor(student.getXp()))
                .currentUser(currentUser)
                .build();
    }

    private GamificationSummaryResponse toSummary(Student student) {
        int xp = student.getXp();
        int xpIntoLevel = xp % XP_PER_LEVEL;
        return GamificationSummaryResponse.builder()
                .xp(xp)
                .level(levelFor(xp))
                .xpIntoLevel(xpIntoLevel)
                .xpForNextLevel(XP_PER_LEVEL)
                .progressPercent(Math.round((xpIntoLevel / (double) XP_PER_LEVEL) * 1000) / 10.0)
                .currentStreak(student.getCurrentStreak())
                .longestStreak(student.getLongestStreak())
                .build();
    }

    private int levelFor(int xp) {
        return 1 + (xp / XP_PER_LEVEL);
    }
}
