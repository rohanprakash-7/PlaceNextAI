package com.placenextai.service.impl;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.placenextai.dto.AdminAnalyticsOverviewResponse;
import com.placenextai.dto.AiPredictionAnalyticsResponse;
import com.placenextai.dto.CollegeAnalyticsResponse;
import com.placenextai.dto.DepartmentAnalyticsResponse;
import com.placenextai.dto.HiringTrendResponse;
import com.placenextai.dto.InterviewStatsResponse;
import com.placenextai.dto.RecruiterActivityResponse;
import com.placenextai.dto.ResumeStatsResponse;
import com.placenextai.dto.RiskDistributionResponse;
import com.placenextai.dto.SkillAnalyticsResponse;
import com.placenextai.dto.StudentAnalyticsResponse;
import com.placenextai.exception.AiServiceException;
import com.placenextai.service.AdminAnalyticsService;
import com.placenextai.service.ReportExportService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportExportServiceImpl implements ReportExportService {

    private final AdminAnalyticsService adminAnalyticsService;

    @Override
    @Transactional(readOnly = true)
    public byte[] exportPdf() {
        AdminAnalyticsOverviewResponse overview = adminAnalyticsService.overview();
        List<DepartmentAnalyticsResponse> departments = adminAnalyticsService.departments();
        List<RecruiterActivityResponse> recruiters = adminAnalyticsService.recruiterActivity();
        RiskDistributionResponse risk = adminAnalyticsService.riskDistribution();
        List<CollegeAnalyticsResponse> colleges = adminAnalyticsService.colleges();
        List<HiringTrendResponse> hiringTrends = adminAnalyticsService.hiringTrends();
        ResumeStatsResponse resumeStats = adminAnalyticsService.resumeStatistics();
        InterviewStatsResponse interviewStats = adminAnalyticsService.interviewStatistics();
        AiPredictionAnalyticsResponse aiPredictions = adminAnalyticsService.aiPredictionAnalytics();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font headingFont = new Font(Font.HELVETICA, 13, Font.BOLD);
            Font bodyFont = new Font(Font.HELVETICA, 10, Font.NORMAL);

            document.add(new Paragraph("PlaceNextAI - Placement Analytics Report", titleFont));
            document.add(new Paragraph("Generated " + LocalDateTime.now(), bodyFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Overview", headingFont));
            document.add(new Paragraph("Total students: " + overview.getTotalStudents(), bodyFont));
            document.add(new Paragraph("Placed: " + overview.getPlacedStudents()
                    + " (" + overview.getPlacementPercent() + "%)", bodyFont));
            document.add(new Paragraph("Average readiness score: " + overview.getAverageReadiness(), bodyFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Placement Risk Distribution", headingFont));
            document.add(new Paragraph("Low: " + risk.getLow()
                    + "   Medium: " + risk.getMedium()
                    + "   High: " + risk.getHigh(), bodyFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Department-wise Readiness", headingFont));
            PdfPTable deptTable = new PdfPTable(3);
            deptTable.setWidthPercentage(100);
            addHeaderRow(deptTable, "Branch", "Students", "Avg. Readiness");
            for (DepartmentAnalyticsResponse department : departments) {
                deptTable.addCell(cell(department.getBranch()));
                deptTable.addCell(cell(String.valueOf(department.getStudentCount())));
                deptTable.addCell(cell(String.valueOf(department.getAverageReadiness())));
            }
            document.add(deptTable);
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Recruiter Activity", headingFont));
            PdfPTable recruiterTable = new PdfPTable(4);
            recruiterTable.setWidthPercentage(100);
            addHeaderRow(recruiterTable, "Company", "Recruiter", "Applications", "Feedback");
            for (RecruiterActivityResponse recruiter : recruiters) {
                recruiterTable.addCell(cell(recruiter.getCompanyName()));
                recruiterTable.addCell(cell(recruiter.getRecruiterName()));
                recruiterTable.addCell(cell(String.valueOf(recruiter.getApplicationsReceived())));
                recruiterTable.addCell(cell(String.valueOf(recruiter.getFeedbackCount())));
            }
            document.add(recruiterTable);
            document.add(new Paragraph(" "));

            document.add(new Paragraph("College-wise Placement", headingFont));
            PdfPTable collegeTable = new PdfPTable(4);
            collegeTable.setWidthPercentage(100);
            addHeaderRow(collegeTable, "College", "Students", "Avg. Readiness", "Placement %");
            for (CollegeAnalyticsResponse college : colleges) {
                collegeTable.addCell(cell(college.getCollege()));
                collegeTable.addCell(cell(String.valueOf(college.getStudentCount())));
                collegeTable.addCell(cell(String.valueOf(college.getAverageReadiness())));
                collegeTable.addCell(cell(String.valueOf(college.getPlacementPercent())));
            }
            document.add(collegeTable);
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Hiring Trends (last 6 months)", headingFont));
            PdfPTable trendTable = new PdfPTable(3);
            trendTable.setWidthPercentage(100);
            addHeaderRow(trendTable, "Month", "Applications", "Hires");
            for (HiringTrendResponse trend : hiringTrends) {
                trendTable.addCell(cell(trend.getMonth()));
                trendTable.addCell(cell(String.valueOf(trend.getApplications())));
                trendTable.addCell(cell(String.valueOf(trend.getHires())));
            }
            document.add(trendTable);
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Resume, Interview & AI Prediction Stats", headingFont));
            document.add(new Paragraph("Resume versions: " + resumeStats.getTotalResumeVersions()
                    + "   Avg. ATS score: " + resumeStats.getAverageAtsScore(), bodyFont));
            document.add(new Paragraph("Mock interviews: " + interviewStats.getTotalMockInterviews()
                    + "   Avg. score: " + interviewStats.getAverageMockInterviewScore()
                    + "   Success stories shared: " + interviewStats.getTotalSuccessStories(), bodyFont));
            document.add(new Paragraph("AI predictions computed: " + aiPredictions.getTotalPredictions()
                    + "   Avg. placement probability: " + aiPredictions.getAverageProbability() + "%", bodyFont));

            document.close();
            return out.toByteArray();
        } catch (Exception exception) {
            throw new AiServiceException("Could not generate the PDF report: " + exception.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportExcel() {
        AdminAnalyticsOverviewResponse overview = adminAnalyticsService.overview();
        List<DepartmentAnalyticsResponse> departments = adminAnalyticsService.departments();
        List<RecruiterActivityResponse> recruiters = adminAnalyticsService.recruiterActivity();
        List<CollegeAnalyticsResponse> colleges = adminAnalyticsService.colleges();
        List<HiringTrendResponse> hiringTrends = adminAnalyticsService.hiringTrends();
        SkillAnalyticsResponse skills = adminAnalyticsService.skillAnalytics();
        StudentAnalyticsResponse studentAnalytics = adminAnalyticsService.studentAnalytics();

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet overviewSheet = workbook.createSheet("Overview");
            Row overviewHeader = overviewSheet.createRow(0);
            writeRow(overviewHeader, "Total Students", "Placed", "Placement %", "Avg. Readiness");
            writeRow(overviewSheet.createRow(1),
                    String.valueOf(overview.getTotalStudents()),
                    String.valueOf(overview.getPlacedStudents()),
                    String.valueOf(overview.getPlacementPercent()),
                    String.valueOf(overview.getAverageReadiness()));

            Sheet deptSheet = workbook.createSheet("Departments");
            writeRow(deptSheet.createRow(0), "Branch", "Students", "Avg. Readiness");
            int deptRow = 1;
            for (DepartmentAnalyticsResponse department : departments) {
                writeRow(deptSheet.createRow(deptRow++),
                        department.getBranch(),
                        String.valueOf(department.getStudentCount()),
                        String.valueOf(department.getAverageReadiness()));
            }

            Sheet recruiterSheet = workbook.createSheet("Recruiters");
            writeRow(recruiterSheet.createRow(0), "Company", "Recruiter", "Applications", "Feedback");
            int recruiterRow = 1;
            for (RecruiterActivityResponse recruiter : recruiters) {
                writeRow(recruiterSheet.createRow(recruiterRow++),
                        recruiter.getCompanyName(),
                        recruiter.getRecruiterName(),
                        String.valueOf(recruiter.getApplicationsReceived()),
                        String.valueOf(recruiter.getFeedbackCount()));
            }

            Sheet collegeSheet = workbook.createSheet("Colleges");
            writeRow(collegeSheet.createRow(0), "College", "Students", "Avg. Readiness", "Placement %");
            int collegeRow = 1;
            for (CollegeAnalyticsResponse college : colleges) {
                writeRow(collegeSheet.createRow(collegeRow++),
                        college.getCollege(),
                        String.valueOf(college.getStudentCount()),
                        String.valueOf(college.getAverageReadiness()),
                        String.valueOf(college.getPlacementPercent()));
            }

            Sheet trendSheet = workbook.createSheet("Hiring Trends");
            writeRow(trendSheet.createRow(0), "Month", "Applications", "Hires");
            int trendRow = 1;
            for (HiringTrendResponse trend : hiringTrends) {
                writeRow(trendSheet.createRow(trendRow++),
                        trend.getMonth(),
                        String.valueOf(trend.getApplications()),
                        String.valueOf(trend.getHires()));
            }

            Sheet skillSheet = workbook.createSheet("Skills");
            writeRow(skillSheet.createRow(0), "Top Student Skills", "Count", "Top Demanded Skills", "Count");
            int skillRow = 1;
            int maxSkillRows = Math.max(skills.getTopStudentSkills().size(), skills.getTopDemandedSkills().size());
            for (int i = 0; i < maxSkillRows; i++) {
                String studentSkill = i < skills.getTopStudentSkills().size() ? skills.getTopStudentSkills().get(i).getSkill() : "";
                String studentCount = i < skills.getTopStudentSkills().size() ? String.valueOf(skills.getTopStudentSkills().get(i).getCount()) : "";
                String demandSkill = i < skills.getTopDemandedSkills().size() ? skills.getTopDemandedSkills().get(i).getSkill() : "";
                String demandCount = i < skills.getTopDemandedSkills().size() ? String.valueOf(skills.getTopDemandedSkills().get(i).getCount()) : "";
                writeRow(skillSheet.createRow(skillRow++), studentSkill, studentCount, demandSkill, demandCount);
            }

            Sheet studentSheet = workbook.createSheet("Student Stats");
            writeRow(studentSheet.createRow(0), "Metric", "Value");
            writeRow(studentSheet.createRow(1), "Total Students", String.valueOf(studentAnalytics.getTotalStudents()));
            writeRow(studentSheet.createRow(2), "Avg Resume Score", String.valueOf(studentAnalytics.getAverageResumeScore()));
            writeRow(studentSheet.createRow(3), "Avg Mock Interview Score", String.valueOf(studentAnalytics.getAverageMockInterviewScore()));
            writeRow(studentSheet.createRow(4), "Avg CGPA", String.valueOf(studentAnalytics.getAverageCgpa()));

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException exception) {
            throw new AiServiceException("Could not generate the Excel report: " + exception.getMessage());
        }
    }

    private void addHeaderRow(PdfPTable table, String... headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Paragraph(header, new Font(Font.HELVETICA, 10, Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
        }
    }

    private PdfPCell cell(String text) {
        return new PdfPCell(new Paragraph(text, new Font(Font.HELVETICA, 10, Font.NORMAL)));
    }

    private void writeRow(Row row, String... values) {
        for (int i = 0; i < values.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(values[i]);
        }
    }
}
