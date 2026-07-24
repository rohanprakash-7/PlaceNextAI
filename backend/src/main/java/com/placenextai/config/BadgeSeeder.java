package com.placenextai.config;

import com.placenextai.entity.Badge;
import com.placenextai.repository.BadgeRepository;
import com.placenextai.service.BadgeService;
import com.placenextai.service.RecruiterBadgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BadgeSeeder implements CommandLineRunner {

    private final BadgeRepository badgeRepository;

    @Override
    public void run(String... args) {
        seed(BadgeService.FIRST_APPLICATION, "First Application", "Submitted your first job application.", "FiSend", "RESUME", 20);
        seed(BadgeService.RESUME_REFINED, "Resume Refined", "Uploaded three or more resume versions.", "FiFileText", "RESUME", 30);
        seed(BadgeService.CONSISTENCY_STREAK, "Consistency Streak", "Completed four or more roadmap items within a month.", "FiTrendingUp", "SKILL", 30);
        seed(BadgeService.INTERVIEW_READY, "Interview Ready", "Completed a mock interview.", "FiMic", "INTERVIEW", 25);
        seed(BadgeService.OFFER_RECEIVED, "Offer Received", "Reached the offer stage on an application.", "FiAward", "PLACEMENT", 50);
        seed(BadgeService.SKILL_MASTER, "Skill Master", "Completed every item in a skill roadmap.", "FiCpu", "SKILL", 60);
        seed(BadgeService.INTERVIEW_ACE, "Interview Ace", "Scored 85 or higher in a mock interview.", "FiStar", "INTERVIEW", 40);
        seed(BadgeService.PLACED, "Placed!", "Got hired through the platform.", "FiCheckCircle", "PLACEMENT", 100);
        seed(BadgeService.MENTORSHIP_STARTER, "Mentorship Starter", "Had your first mentorship request accepted.", "FiUsers", "MILESTONE", 20);
        seed(BadgeService.STREAK_7, "7-Day Streak", "Stayed active on the platform for 7 days in a row.", "FiZap", "MILESTONE", 30);
        seed(BadgeService.STREAK_30, "30-Day Streak", "Stayed active on the platform for 30 days in a row.", "FiZap", "MILESTONE", 100);

        seed(RecruiterBadgeService.JOB_POSTER, "Job Poster", "Posted your first job opening.", "FiBriefcase", "RECRUITER", 0);
        seed(RecruiterBadgeService.ACTIVE_RECRUITER, "Active Recruiter", "Received 20 or more applications.", "FiUsers", "RECRUITER", 0);
        seed(RecruiterBadgeService.TOP_HIRER, "Top Hirer", "Hired a candidate through the platform.", "FiAward", "RECRUITER", 0);
    }

    private void seed(String code, String name, String description, String icon, String category, int xpReward) {
        if (badgeRepository.findByCode(code).isEmpty()) {
            badgeRepository.save(Badge.builder()
                    .code(code)
                    .name(name)
                    .description(description)
                    .icon(icon)
                    .category(category)
                    .xpReward(xpReward)
                    .build());
        }
    }
}
