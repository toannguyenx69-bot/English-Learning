package com.example.englishlearning.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface PdfStorageService {

    StoredPdf save(MultipartFile file, String ownerEmail);

    StoredPdf getByFileId(String fileId);

    InputStream load(String fileId);

    void delete(String fileId);

    boolean isOwner(String fileId, String ownerEmail);

    record StoredPdf(
            String fileId,
            String fileName,
            String originalFileName,
            long size,
            String contentType,
            String storagePath,
            String ownerEmail
    ) {
    }
}
