package com.example.englishlearning.dto;

public record PdfUploadResponse(
        String fileId,
        String fileName,
        long size,
        String message
) {
}
