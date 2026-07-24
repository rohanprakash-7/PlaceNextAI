package com.placenextai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "interview_questions",
        indexes = @Index(name = "idx_interview_question_session", columnList = "sessionId, questionOrder")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long sessionId;

    @Column(nullable = false)
    private int questionOrder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InterviewQuestionCategory category;

    @Column(nullable = false, length = 500)
    private String questionText;

    @Column(length = 500)
    private String expectedKeywords;

    @Column(length = 3000)
    private String studentAnswer;

    private Integer score;

    @Column(length = 500)
    private String feedback;

    private LocalDateTime answeredAt;
}
