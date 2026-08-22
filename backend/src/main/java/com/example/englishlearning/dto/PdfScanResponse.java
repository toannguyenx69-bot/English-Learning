package com.example.englishlearning.dto;

import java.util.List;

public record PdfScanResponse(
        String fileId,
        String fileName,
        String scanMode,
        List<BoldTextDto> boldTexts
) {
}
