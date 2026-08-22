package com.example.englishlearning.service;

import com.example.englishlearning.dto.BoldTextDto;
import com.example.englishlearning.dto.PdfScanMode;
import com.example.englishlearning.dto.PdfScanRequest;
import com.example.englishlearning.dto.PdfScanResponse;
import com.example.englishlearning.dto.PdfUploadResponse;
import com.example.englishlearning.exception.PdfProcessingException;
import com.example.englishlearning.exception.PdfValidationException;
import com.example.englishlearning.storage.PdfStorageService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationTextMarkup;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PdfProcessingService {

    private final PdfStorageService pdfStorageService;
    private final PdfBoldTextDetector pdfBoldTextDetector;

    public PdfProcessingService(PdfStorageService pdfStorageService, PdfBoldTextDetector pdfBoldTextDetector) {
        this.pdfStorageService = pdfStorageService;
        this.pdfBoldTextDetector = pdfBoldTextDetector;
    }

    public PdfUploadResponse uploadPdf(MultipartFile file, String userEmail) {
        if (file == null || file.isEmpty()) {
            throw new PdfValidationException("Please select a PDF file to upload.");
        }

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.isBlank()) {
            throw new PdfValidationException("Uploaded file is missing a valid name.");
        }

        String lowerName = originalFileName.toLowerCase();
        if (!lowerName.endsWith(".pdf")) {
            throw new PdfValidationException("Only PDF files are allowed.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.equalsIgnoreCase("application/pdf")) {
            throw new PdfValidationException("The selected file is not a valid PDF document.");
        }

        try {
            PdfStorageService.StoredPdf storedPdf = pdfStorageService.save(file, userEmail);
            return new PdfUploadResponse(
                    storedPdf.fileId(),
                    storedPdf.originalFileName(),
                    storedPdf.size(),
                    "PDF uploaded successfully"
            );
        } catch (Exception ex) {
            throw new PdfProcessingException("Failed to upload PDF file.", ex);
        }
    }

    public PdfScanResponse scanPdf(String fileId, String userEmail) {
        return scanPdf(fileId, userEmail, null);
    }

    public PdfScanResponse scanPdf(String fileId, String userEmail, PdfScanRequest request) {
        if (fileId == null || fileId.isBlank()) {
            throw new PdfValidationException("Invalid PDF file reference.");
        }

        PdfStorageService.StoredPdf storedPdf = pdfStorageService.getByFileId(fileId);
        if (!pdfStorageService.isOwner(fileId, userEmail)) {
            throw new PdfValidationException("You are not authorized to scan this PDF file.");
        }

        PdfScanMode scanMode = request == null || request.scanMode() == null ? PdfScanMode.BOLD : request.scanMode();
        List<PdfBoldTextDetector.PdfTextFragment> fragments = new ArrayList<>();

        try (InputStream inputStream = pdfStorageService.load(fileId);
             PDDocument document = PDDocument.load(inputStream)) {
            if (document.isEncrypted()) {
                throw new PdfValidationException("The PDF is encrypted and cannot be scanned.");
            }

            Map<Integer, List<HighlightRegion>> highlightRegionsByPage = collectHighlightRegions(document);

            PDFTextStripper stripper = new PDFTextStripper() {
                @Override
                protected void writeString(String string, List<TextPosition> textPositions) {
                    if (string == null || string.isBlank()) {
                        return;
                    }

                    for (TextPosition textPosition : textPositions) {
                        if (textPosition == null || textPosition.getUnicode() == null) {
                            continue;
                        }

                        String unicode = textPosition.getUnicode();
                        if (unicode.isBlank()) {
                            continue;
                        }

                        int pageNo = getCurrentPageNo();
                        double x = textPosition.getXDirAdj();
                        double y = textPosition.getYDirAdj();
                        double width = textPosition.getWidthDirAdj();
                        double height = textPosition.getHeightDir();
                        double minX = x;
                        double maxX = x + width;
                        double minY = y;
                        double maxY = y + height;
                        boolean highlighted = overlapsHighlightRegion(pageNo, minX, minY, maxX, maxY, highlightRegionsByPage);

                        String fontName = textPosition.getFont() == null ? "" : textPosition.getFont().getName();
                        fragments.add(new PdfBoldTextDetector.PdfTextFragment(
                                pageNo,
                                unicode,
                                fontName,
                                (double) x,
                                (double) y,
                                (double) width,
                                (double) height,
                                textPosition.getFont() != null && textPosition.getFont().getFontDescriptor() != null
                                        ? textPosition.getFont().getFontDescriptor().isItalic()
                                        : false,
                                highlighted
                        ));
                    }
                }
            };

            stripper.setSortByPosition(true);
            stripper.getText(document);

            List<BoldTextDto> detected = switch (scanMode) {
                case BOLD -> pdfBoldTextDetector.detectBoldText(fragments);
                case ITALIC -> pdfBoldTextDetector.detectItalicText(fragments);
                case HIGHLIGHT -> pdfBoldTextDetector.detectHighlightText(fragments);
            };

            return new PdfScanResponse(fileId, storedPdf.originalFileName(), scanMode.name(), detected);
        } catch (IOException ex) {
            throw new PdfProcessingException("Failed to parse the uploaded PDF file.", ex);
        }
    }

    private Map<Integer, List<HighlightRegion>> collectHighlightRegions(PDDocument document) throws IOException {
        Map<Integer, List<HighlightRegion>> byPage = new HashMap<>();

        for (int pageIndex = 0; pageIndex < document.getNumberOfPages(); pageIndex++) {
            PDPage page = document.getPage(pageIndex);
            List<PDAnnotation> annotations = page.getAnnotations();
            if (annotations == null || annotations.isEmpty()) {
                continue;
            }

            List<HighlightRegion> pageRegions = new ArrayList<>();
            for (PDAnnotation annotation : annotations) {
                if (annotation == null || !(annotation instanceof PDAnnotationTextMarkup textMarkup)) {
                    continue;
                }

                String subtype = textMarkup.getSubtype();
                if (subtype == null) {
                    continue;
                }

                String normalizeSubtype = subtype.toLowerCase();
                if (!normalizeSubtype.contains("highlight")
                        && !normalizeSubtype.contains("underline")
                        && !normalizeSubtype.contains("squiggly")
                        && !normalizeSubtype.contains("strike")) {
                    continue;
                }

                float[] quadPoints = textMarkup.getQuadPoints();
                if (quadPoints == null || quadPoints.length < 8) {
                    continue;
                }

                double minX = Double.MAX_VALUE;
                double minY = Double.MAX_VALUE;
                double maxX = Double.MIN_VALUE;
                double maxY = Double.MIN_VALUE;
                for (int i = 0; i < quadPoints.length; i += 2) {
                    double x = quadPoints[i];
                    double y = quadPoints[i + 1];
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }

                if (Double.isFinite(minX) && Double.isFinite(minY) && Double.isFinite(maxX) && Double.isFinite(maxY)) {
                    pageRegions.add(new HighlightRegion(minX, minY, maxX, maxY, subtype, textMarkup.getColor()));
                }
            }

            if (!pageRegions.isEmpty()) {
                byPage.put(pageIndex + 1, pageRegions);
            }
        }

        return byPage;
    }

    private boolean overlapsHighlightRegion(int pageNo,
                                           double textMinX,
                                           double textMinY,
                                           double textMaxX,
                                           double textMaxY,
                                           Map<Integer, List<HighlightRegion>> highlightRegionsByPage) {
        List<HighlightRegion> regions = highlightRegionsByPage.get(pageNo);
        if (regions == null || regions.isEmpty()) {
            return false;
        }

        for (HighlightRegion region : regions) {
            boolean overlaps = textMaxX >= region.minX() && textMinX <= region.maxX()
                    && textMaxY >= region.minY() && textMinY <= region.maxY();
            if (overlaps) {
                return true;
            }
        }

        return false;
    }

    private record HighlightRegion(double minX, double minY, double maxX, double maxY, String subtype, org.apache.pdfbox.pdmodel.graphics.color.PDColor color) {
    }
}
