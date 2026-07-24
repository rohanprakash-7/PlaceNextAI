package com.placenextai.service.impl;

import com.placenextai.dto.GamificationSummaryResponse;
import com.placenextai.entity.EventType;
import com.placenextai.entity.Student;
import com.placenextai.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GamificationServiceImplTest {

    @Mock
    private StudentRepository studentRepository;

    private GamificationServiceImpl gamificationService;
    private Student student;

    @BeforeEach
    void setUp() {
        gamificationService = new GamificationServiceImpl(studentRepository);
        student = Student.builder()
                .id(1L)
                .email("student@test.com")
                .xp(0)
                .currentStreak(0)
                .longestStreak(0)
                .build();
        lenient().when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        lenient().when(studentRepository.findByEmail("student@test.com")).thenReturn(Optional.of(student));
        lenient().when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void firstEverActivity_startsStreakAtOne() {
        int streak = gamificationService.recordActivity(1L, EventType.RESUME_UPLOADED);

        assertThat(streak).isEqualTo(1);
        assertThat(student.getCurrentStreak()).isEqualTo(1);
        assertThat(student.getLongestStreak()).isEqualTo(1);
        assertThat(student.getXp()).isEqualTo(15); // RESUME_UPLOADED awards 15 XP.
    }

    @Test
    void secondActivitySameDay_doesNotIncrementStreakButStillAwardsXp() {
        student.setLastActivityDate(LocalDate.now());
        student.setCurrentStreak(1);

        int streak = gamificationService.recordActivity(1L, EventType.APPLICATION_SUBMITTED);

        assertThat(streak).isEqualTo(1);
        assertThat(student.getXp()).isEqualTo(10); // APPLICATION_SUBMITTED awards 10 XP regardless of streak.
    }

    @Test
    void activityOnConsecutiveDay_incrementsStreak() {
        student.setLastActivityDate(LocalDate.now().minusDays(1));
        student.setCurrentStreak(4);
        student.setLongestStreak(4);

        int streak = gamificationService.recordActivity(1L, EventType.LOGIN);

        assertThat(streak).isEqualTo(5);
        assertThat(student.getLongestStreak()).isEqualTo(5);
    }

    @Test
    void activityAfterGap_resetsStreakToOne() {
        student.setLastActivityDate(LocalDate.now().minusDays(3));
        student.setCurrentStreak(10);
        student.setLongestStreak(10);

        int streak = gamificationService.recordActivity(1L, EventType.LOGIN);

        assertThat(streak).isEqualTo(1);
        assertThat(student.getLongestStreak()).isEqualTo(10); // Longest streak is preserved, not reset.
    }

    @Test
    void getSummary_computesLevelAndProgressFromXp() {
        student.setXp(250); // Level = 1 + 250/100 = 3, 50 XP into the level.

        GamificationSummaryResponse summary = gamificationService.getSummary("student@test.com");

        assertThat(summary.getLevel()).isEqualTo(3);
        assertThat(summary.getXpIntoLevel()).isEqualTo(50);
        assertThat(summary.getXpForNextLevel()).isEqualTo(100);
        assertThat(summary.getProgressPercent()).isEqualTo(50.0);
    }
}
