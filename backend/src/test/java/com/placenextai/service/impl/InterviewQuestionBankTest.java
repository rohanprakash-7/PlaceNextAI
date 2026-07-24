package com.placenextai.service.impl;

import com.placenextai.entity.InterviewQuestionCategory;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class InterviewQuestionBankTest {

    private final InterviewQuestionBank bank = new InterviewQuestionBank();

    @Test
    void generalInterview_withKnownSkills_startsWithBehavioralAndCoversEachSkill() {
        List<InterviewQuestionBank.GeneratedQuestion> questions =
                bank.generate(Set.of("Java", "SQL"), null, Set.of());

        assertThat(questions.get(0).category()).isEqualTo(InterviewQuestionCategory.BEHAVIORAL);
        assertThat(questions.get(questions.size() - 1).category()).isEqualTo(InterviewQuestionCategory.BEHAVIORAL);
        assertThat(questions).filteredOn(q -> q.category() == InterviewQuestionCategory.TECHNICAL).hasSize(2);
        assertThat(questions).allSatisfy(q -> assertThat(q.keywords()).isNotEmpty());
    }

    @Test
    void generalInterview_withNoSkills_stillProducesAFallbackTechnicalQuestion() {
        List<InterviewQuestionBank.GeneratedQuestion> questions = bank.generate(Set.of(), null, Set.of());

        assertThat(questions).anySatisfy(q -> assertThat(q.category()).isEqualTo(InterviewQuestionCategory.TECHNICAL));
    }

    @Test
    void companyTargetedInterview_asksAboutMatchedSkillsAndGapsInMissingOnes() {
        Set<String> studentSkills = Set.of("Java");
        Set<String> companyRequiredSkills = Set.of("Java", "AWS");

        List<InterviewQuestionBank.GeneratedQuestion> questions =
                bank.generate(studentSkills, "Amazon", companyRequiredSkills);

        boolean hasCompanyFitClosing = questions.stream()
                .anyMatch(q -> q.text().contains("Why do you want to work at Amazon"));
        boolean hasGapQuestionForAws = questions.stream()
                .anyMatch(q -> q.category() == InterviewQuestionCategory.COMPANY_FIT && q.text().contains("AWS"));

        assertThat(hasCompanyFitClosing).isTrue();
        assertThat(hasGapQuestionForAws).isTrue();
    }

    @Test
    void unknownSkill_fallsBackToGenericProjectQuestion() {
        List<InterviewQuestionBank.GeneratedQuestion> questions =
                bank.generate(Set.of("SomeObscureFramework"), null, Set.of());

        assertThat(questions)
                .anySatisfy(q -> assertThat(q.text()).contains("SomeObscureFramework"));
    }
}
