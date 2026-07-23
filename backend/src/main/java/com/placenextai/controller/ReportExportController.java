package com.placenextai.controller;

import com.placenextai.service.ReportExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
public class ReportExportController {

    private final ReportExportService reportExportService;

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(@RequestParam(defaultValue = "pdf") String format) {
        boolean excel = "xlsx".equalsIgnoreCase(format);

        byte[] content = excel ? reportExportService.exportExcel() : reportExportService.exportPdf();
        String filename = "placenextai-report." + (excel ? "xlsx" : "pdf");
        MediaType mediaType = excel
                ? MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                : MediaType.APPLICATION_PDF;

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(filename).build().toString())
                .body(content);
    }
}
