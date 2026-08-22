package com.example.englishlearning.service;

import com.example.englishlearning.dto.BoldTextDto;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelExportService {

    public byte[] exportToXlsx(List<BoldTextDto> items) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Bold Paragraphs");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Page");
            header.createCell(1).setCellValue("Text");

            if (items != null) {
                for (int i = 0; i < items.size(); i++) {
                    BoldTextDto item = items.get(i);
                    Row row = sheet.createRow(i + 1);
                    row.createCell(0).setCellValue(item.page() == null ? 0 : item.page());
                    row.createCell(1).setCellValue(item.text() == null ? "" : item.text());
                }
            }

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to generate Excel export for bold paragraphs.", ex);
        }
    }
}
