package com.placenextai.service.impl;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.placenextai.dto.AdminAnalyticsOverviewResponse;
import com.placenextai.dto.DepartmentAnalyticsResponse;
import com.placenextai.dto.RecruiterActivityResponse;
import com.placenextai.dto.RiskDistributionResponse;
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
