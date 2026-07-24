package com.placenextai.service.impl;

import com.placenextai.dto.AdminAnalyticsOverviewResponse;
import com.placenextai.dto.AiPredictionAnalyticsResponse;
import com.placenextai.dto.CollegeAnalyticsResponse;
import com.placenextai.dto.DayCountResponse;
import com.placenextai.dto.DepartmentAnalyticsResponse;
import com.placenextai.dto.HiringTrendResponse;
import com.placenextai.dto.InterviewStatsResponse;
import com.placenextai.dto.RecruiterActivityResponse;
import com.placenextai.dto.ResumeStatsResponse;
import com.placenextai.dto.RiskDistributionResponse;
import com.placenextai.dto.SkillAnalyticsResponse;
import com.placenextai.dto.SkillCountResponse;
import com.placenextai.dto.StudentAnalyticsResponse;
import com.placenextai.entity.Application;
import com.placenextai.entity.ApplicationStatus;
import com.placenextai.entity.InterviewExperience;
import com.placenextai.entity.Job;
import com.placenextai.entity.PlacementPrediction;
import com.placenextai.entity.PlacementStatus;
import com.placenextai.entity.Recruiter;
import com.placenextai.entity.ResumeVersion;
import com.placenextai.entity.RiskLevel;
import com.placenextai.entity.Student;
import com.placenextai.repository.ApplicationRepository;
import com.placenextai.repository.InterviewExperienceRepository;
import com.placenextai.repository.JobRepository;
import com.placenextai.repository.PlacementPredictionRepository;
import com.placenextai.repository.PlatformEventRepository;
import com.placenextai.repository.ReadinessScoreRepository;
import com.placenextai.repository.RecruiterFeedbackRepository;
import com.placenextai.repository.RecruiterRepository;
import com.placenextai.repository.ResumeVersionRepository;
import com.placenextai.repository.StudentRepository;
import com.placenextai.service.AdminAnalyticsService;
import com.placenextai.service.PlacementPredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminAnalyticsServiceImpl implements AdminAnalyticsService {

    private final StudentRepository studentRepository;
    private final RecruiterRepository recruiterRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final ReadinessScoreRepository readinessScoreRepository;
    private final RecruiterFeedbackRepository feedbackRepository;
    private final PlacementPredictionService placementPredictionService;
    private final ResumeVersionRepository resumeVersionRepository;
    private final PlacementPredictionRepository placementPredictionRepository;
    private final InterviewExperienceRepository interviewExperienceRepository;
    private final PlatformEventRepository platformEventRepository;

    @Override
    @Transactional(readOnly = true)
    public AdminAnalyticsOverviewResponse overview() {
        List<Student> students = studentRepository.findAll();
        long placed = students.stream()
                .filter(student -> student.getPlacementStatus() == PlacementStatus.PLACED)
                .count();

        return AdminAnalyticsOverviewResponse.builder()
                .totalStudents(students.size())
                .placedStudents(placed)
                .placementPercent(students.isEmpty() ? 0 : round1(100.0 * placed / students.size()))
                .averageReadiness(averageReadiness(students))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentAnalyticsResponse> departments() {
        List<Student> students = studentRepository.findAll();
        Map<String, List<Student>> byBranch = students.stream()
                .collect(Collectors.groupingBy(student ->
                        student.getBranch() == null || student.getBranch().isBlank() ? "Unspecified" : student.getBranch()));

        return byBranch.entrySet().stream()
                .map(entry -> DepartmentAnalyticsResponse.builder()
                        .branch(entry.getKey())
                        .studentCount(entry.getValue().size())
                        .averageReadiness(averageReadiness(entry.getValue()))
                        .build())
                .sorted((a, b) -> Long.compare(b.getStudentCount(), a.getStudentCount()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecruiterActivityResponse> recruiterActivity() {
        List<Recruiter> recruiters = recruiterRepository.findAll();

        return recruiters.stream()
                .map(recruiter -> {
                    List<Job> companyJobs = jobRepository
                            .findByCompanyIgnoreCaseOrderByCreatedDateDesc(recruiter.getCompanyName());
                    long applicationsReceived = companyJobs.isEmpty()
                            ? 0
                            : applicationRepository.findByJobInOrderByAppliedDateDesc(companyJobs).size();

                    return RecruiterActivityResponse.builder()
                            .recruiterId(recruiter.getId())
                            .companyName(recruiter.getCompanyName())
                            .recruiterName(recruiter.getRecruiterName())
                            .feedbackCount(feedbackRepository.countByRecruiterId(recruiter.getId()))
                            .applicationsReceived(applicationsReceived)
                            .build();
                })
                .sorted((a, b) -> Long.compare(b.getApplicationsReceived(), a.getApplicationsReceived()))
                .toList();
    }

    @Override
    @Transactional
    public RiskDistributionResponse riskDistribution() {
        List<Student> students = studentRepository.findAll();
        long low = 0;
        long medium = 0;
        long high = 0;

        for (Student student : students) {
            RiskLevel risk = RiskLevel.valueOf(
                    placementPredictionService.getOrComputeLatest(student).getRiskLevel());
            switch (risk) {
                case LOW -> low++;
                case MEDIUM -> medium++;
                case HIGH -> high++;
            }
        }

        return RiskDistributionResponse.builder().low(low).medium(medium).high(high).build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CollegeAnalyticsResponse> colleges() {
        List<Student> students = studentRepository.findAll();
        Map<String, List<Student>> byCollege = students.stream()
                .collect(Collectors.groupingBy(student ->
                        student.getCollege() == null || student.getCollege().isBlank() ? "Unspecified" : student.getCollege()));

        return byCollege.entrySet().stream()
                .map(entry -> {
                    List<Student> group = entry.getValue();
                    long placed = group.stream()
                            .filter(student -> student.getPlacementStatus() == PlacementStatus.PLACED)
                            .count();
                    return CollegeAnalyticsResponse.builder()
                            .college(entry.getKey())
                            .studentCount(group.size())
                            .averageReadiness(averageReadiness(group))
                            .placementPercent(group.isEmpty() ? 0 : round1(100.0 * placed / group.size()))
                            .build();
                })
                .sorted((a, b) -> Long.compare(b.getStudentCount(), a.getStudentCount()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StudentAnalyticsResponse studentAnalytics() {
        List<Student> students = studentRepository.findAll();

        double avgResume = students.stream()
                .filter(student -> student.getResumeScore() != null)
                .mapToInt(Student::getResumeScore)
                .average().orElse(0);
        double avgInterview = students.stream()
                .filter(student -> student.getMockInterviewScore() != null)
                .mapToInt(Student::getMockInterviewScore)
                .average().orElse(0);
        double avgCgpa = students.stream()
                .filter(student -> student.getCgpa() != null)
                .mapToDouble(Student::getCgpa)
                .average().orElse(0);

        Map<String, Long> statusBreakdown = new LinkedHashMap<>();
        for (PlacementStatus status : PlacementStatus.values()) {
            statusBreakdown.put(status.name(), students.stream()
                    .filter(student -> student.getPlacementStatus() == status)
                    .count());
        }

        return StudentAnalyticsResponse.builder()
                .totalStudents(students.size())
                .averageResumeScore(round1(avgResume))
                .averageMockInterviewScore(round1(avgInterview))
                .averageCgpa(round1(avgCgpa))
                .placementStatusBreakdown(statusBreakdown)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<HiringTrendResponse> hiringTrends() {
        DateTimeFormatter monthFormat = DateTimeFormatter.ofPattern("yyyy-MM");
        LocalDate cutoff = LocalDate.now().minusMonths(5).withDayOfMonth(1);
        LocalDateTime cutoffDateTime = cutoff.atStartOfDay();

        List<Application> applications = applicationRepository.findAll().stream()
                .filter(application -> application.getAppliedDate() != null
                        && !application.getAppliedDate().isBefore(cutoffDateTime))
                .toList();

        Map<String, long[]> byMonth = new TreeMap<>();
        for (int i = 0; i < 6; i++) {
            byMonth.put(cutoff.plusMonths(i).format(monthFormat), new long[]{0, 0});
        }

        for (Application application : applications) {
            String key = application.getAppliedDate().format(monthFormat);
            long[] counts = byMonth.get(key);
            if (counts == null) {
                continue;
            }
            counts[0]++;
            if (application.getStatus() == ApplicationStatus.HIRED) {
                counts[1]++;
            }
        }

        List<HiringTrendResponse> trends = new ArrayList<>();
        byMonth.forEach((month, counts) -> trends.add(HiringTrendResponse.builder()
                .month(month)
                .applications(counts[0])
                .hires(counts[1])
                .build()));
        return trends;
    }

    @Override
    @Transactional(readOnly = true)
    public ResumeStatsResponse resumeStatistics() {
        List<ResumeVersion> versions = resumeVersionRepository.findAll();
        long studentsWithResume = versions.stream().map(ResumeVersion::getStudentId).distinct().count();
        double avgScore = versions.stream().mapToInt(ResumeVersion::getAtsScore).average().orElse(0);

        return ResumeStatsResponse.builder()
                .totalResumeVersions(versions.size())
                .studentsWithResume(studentsWithResume)
                .averageAtsScore(round1(avgScore))
                .averageVersionsPerStudent(studentsWithResume == 0 ? 0 : round1((double) versions.size() / studentsWithResume))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public InterviewStatsResponse interviewStatistics() {
        long totalMockInterviews = platformEventRepository.countByEventType(com.placenextai.entity.EventType.MOCK_INTERVIEW_COMPLETED);
        double avgScore = studentRepository.findAll().stream()
                .filter(student -> student.getMockInterviewScore() != null)
                .mapToInt(Student::getMockInterviewScore)
                .average().orElse(0);

        List<InterviewExperience> stories = interviewExperienceRepository.findAll();
        Map<String, Long> topCompanies = stories.stream()
                .collect(Collectors.groupingBy(InterviewExperience::getCompany, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));

        return InterviewStatsResponse.builder()
                .totalMockInterviews(totalMockInterviews)
                .averageMockInterviewScore(round1(avgScore))
                .totalSuccessStories(stories.size())
                .topCompaniesByStories(topCompanies)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public SkillAnalyticsResponse skillAnalytics() {
        List<Student> students = studentRepository.findAll();
        List<Job> jobs = jobRepository.findAll();

        return SkillAnalyticsResponse.builder()
                .topStudentSkills(topSkills(students.stream().map(Student::getSkills).toList()))
                .topDemandedSkills(topSkills(jobs.stream().map(Job::getSkillsRequired).toList()))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AiPredictionAnalyticsResponse aiPredictionAnalytics() {
        List<PlacementPrediction> predictions = placementPredictionRepository.findAll();
        double avgProbability = predictions.stream()
                .mapToInt(PlacementPrediction::getProbabilityScore)
                .average().orElse(0);

        long low = predictions.stream().filter(prediction -> prediction.getRiskLevel() == RiskLevel.LOW).count();
        long medium = predictions.stream().filter(prediction -> prediction.getRiskLevel() == RiskLevel.MEDIUM).count();
        long high = predictions.stream().filter(prediction -> prediction.getRiskLevel() == RiskLevel.HIGH).count();

        return AiPredictionAnalyticsResponse.builder()
                .totalPredictions(predictions.size())
                .averageProbability(round1(avgProbability))
                .lowRisk(low)
                .mediumRisk(medium)
                .highRisk(high)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DayCountResponse> platformHeatmap(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return platformEventRepository.countByDaySinceGlobal(since).stream()
                .map(row -> DayCountResponse.builder()
                        .date(toLocalDate(row[0]))
                        .count(((Number) row[1]).longValue())
                        .build())
                .toList();
    }

    private LocalDate toLocalDate(Object rawDate) {
        if (rawDate instanceof Date sqlDate) {
            return sqlDate.toLocalDate();
        }
        return (LocalDate) rawDate;
    }

    private List<SkillCountResponse> topSkills(List<String> rawSkillLists) {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (String raw : rawSkillLists) {
            if (raw == null || raw.isBlank()) {
                continue;
            }
            for (String skill : raw.split(",")) {
                String trimmed = skill.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                counts.merge(trimmed, 1L, Long::sum);
            }
        }
        return counts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .map(entry -> SkillCountResponse.builder().skill(entry.getKey()).count(entry.getValue()).build())
                .toList();
    }

    private double averageReadiness(List<Student> students) {
        if (students.isEmpty()) {
            return 0;
        }
        double sum = students.stream()
                .mapToInt(student -> readinessScoreRepository
                        .findTopByStudentIdOrderByComputedAtDesc(student.getId())
                        .map(score -> score.getTotalScore())
                        .orElse(0))
                .sum();
        return round1(sum / students.size());
    }

    private double round1(double value) {
        return Math.round(value * 10) / 10.0;
    }
}
