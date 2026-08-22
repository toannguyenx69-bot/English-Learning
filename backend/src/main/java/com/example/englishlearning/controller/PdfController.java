package com.example.englishlearning.controller;

import com.example.englishlearning.dto.PdfScanRequest;
import com.example.englishlearning.dto.PdfScanResponse;
import com.example.englishlearning.dto.PdfUploadResponse;
import com.example.englishlearning.service.ExcelExportService;
import com.example.englishlearning.service.PdfProcessingService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/pdf")
public class PdfController {

    private final PdfProcessingService pdfProcessingService;
    private final ExcelExportService excelExportService;

    public PdfController(PdfProcessingService pdfProcessingService, ExcelExportService excelExportService) {
        this.pdfProcessingService = pdfProcessingService;
        this.excelExportService = excelExportService;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/upload")
    public ResponseEntity<PdfUploadResponse> uploadPdf(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        String userEmail = authentication.getName();
        PdfUploadResponse response = pdfProcessingService.uploadPdf(file, userEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{fileId}/scan")
    public ResponseEntity<PdfScanResponse> scanPdf(
            @PathVariable String fileId,
            @RequestBody(required = false) PdfScanRequest request,
            Authentication authentication) {
        String userEmail = authentication.getName();
        PdfScanResponse response = pdfProcessingService.scanPdf(fileId, userEmail, request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{fileId}/export-excel")
    public ResponseEntity<byte[]> exportExcel(
            @PathVariable String fileId,
            Authentication authentication) {
        String userEmail = authentication.getName();
        PdfScanResponse response = pdfProcessingService.scanPdf(fileId, userEmail, null);

        byte[] excelBytes = excelExportService.exportToXlsx(response.boldTexts());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDisposition(ContentDisposition.attachment().filename("bold-paragraphs.xlsx").build());

        return ResponseEntity.ok().headers(headers).body(excelBytes);
    }
}
