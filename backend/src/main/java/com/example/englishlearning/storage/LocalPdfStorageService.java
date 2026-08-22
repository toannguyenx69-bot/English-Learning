package com.example.englishlearning.storage;

import com.example.englishlearning.config.PdfProperties;
import com.example.englishlearning.exception.PdfStorageException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LocalPdfStorageService implements PdfStorageService {

    private final PdfProperties pdfProperties;
    private final Map<String, StoredPdf> storedPdfs = new ConcurrentHashMap<>();

    public LocalPdfStorageService(PdfProperties pdfProperties) {
        this.pdfProperties = pdfProperties;
    }

    @Override
    public StoredPdf save(MultipartFile file, String ownerEmail) {
        if (file == null || file.isEmpty()) {
            throw new PdfStorageException("Uploaded PDF file is empty.");
        }

        if (file.getSize() > pdfProperties.getMaxFileSize()) {
            throw new PdfStorageException("PDF file exceeds the maximum allowed size.");
        }

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.isBlank()) {
            throw new PdfStorageException("Uploaded PDF file is missing a valid name.");
        }

        Path uploadDir = Paths.get(pdfProperties.getUploadDir());
        try {
            Files.createDirectories(uploadDir);
            String fileId = UUID.randomUUID().toString();
            String generatedFileName = fileId + ".pdf";
            Path target = uploadDir.resolve(generatedFileName);
            Files.copy(file.getInputStream(), target);

            StoredPdf storedPdf = new StoredPdf(
                    fileId,
                    generatedFileName,
                    originalFileName,
                    file.getSize(),
                    file.getContentType(),
                    target.toString(),
                    ownerEmail
            );
            storedPdfs.put(fileId, storedPdf);
            return storedPdf;
        } catch (IOException ex) {
            throw new PdfStorageException("Failed to store uploaded PDF file.", ex);
        }
    }

    @Override
    public StoredPdf getByFileId(String fileId) {
        StoredPdf storedPdf = storedPdfs.get(fileId);
        if (storedPdf == null) {
            throw new PdfStorageException("The PDF file could not be found.");
        }
        return storedPdf;
    }

    @Override
    public InputStream load(String fileId) {
        StoredPdf storedPdf = getByFileId(fileId);
        try {
            return Files.newInputStream(Path.of(storedPdf.storagePath()));
        } catch (IOException ex) {
            throw new PdfStorageException("Failed to read the PDF file from storage.", ex);
        }
    }

    @Override
    public void delete(String fileId) {
        StoredPdf storedPdf = getByFileId(fileId);
        try {
            Files.deleteIfExists(Path.of(storedPdf.storagePath()));
            storedPdfs.remove(fileId);
        } catch (IOException ex) {
            throw new PdfStorageException("Failed to delete the PDF file from storage.", ex);
        }
    }

    @Override
    public boolean isOwner(String fileId, String ownerEmail) {
        StoredPdf storedPdf = storedPdfs.get(fileId);
        return storedPdf != null && storedPdf.ownerEmail() != null && storedPdf.ownerEmail().equals(ownerEmail);
    }
}
