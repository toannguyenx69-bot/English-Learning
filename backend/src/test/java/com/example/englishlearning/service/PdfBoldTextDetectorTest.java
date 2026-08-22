package com.example.englishlearning.service;

import com.example.englishlearning.dto.BoldTextDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PdfBoldTextDetectorTest {

    private final PdfBoldTextDetector detector = new PdfBoldTextDetector();

    @Test
    void shouldMergeConsecutiveBoldLettersIntoWords() {
        List<PdfBoldTextDetector.PdfTextFragment> fragments = List.of(
                new PdfBoldTextDetector.PdfTextFragment(1, "P", "Arial-BoldMT", 10, 100, 8, 12, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, "r", "Arial-BoldMT", 18, 100, 8, 12, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, "e", "Arial-BoldMT", 26, 100, 8, 12, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, "s", "Arial-BoldMT", 34, 100, 8, 12, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, "e", "Arial-BoldMT", 42, 100, 8, 12, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, "n", "Arial-BoldMT", 50, 100, 8, 12, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, "t", "Arial-BoldMT", 58, 100, 8, 12, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, " ", "Arial-BoldMT", 66, 100, 8, 12, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, "S", "Arial-BoldMT", 74, 100, 8, 12, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, "i", "Arial-BoldMT", 82, 100, 8, 12, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, "m", "Arial-BoldMT", 90, 100, 8, 12, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, "p", "Arial-BoldMT", 98, 100, 8, 12, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, "l", "Arial-BoldMT", 106, 100, 8, 12, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, "e", "Arial-BoldMT", 114, 100, 8, 12, false, false)
        );

        List<BoldTextDto> results = detector.detectBoldText(fragments);

        assertEquals(1, results.size());
        assertEquals("Present Simple", results.get(0).text());
    }

    @Test
    void shouldKeepSpaceBetweenSeparateBoldWords() {
        List<PdfBoldTextDetector.PdfTextFragment> fragments = List.of(
                new PdfBoldTextDetector.PdfTextFragment(1, "f", "Arial-BoldMT", 10, 100, 8, 12, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, "i", "Arial-BoldMT", 18, 100, 8, 12, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, "n", "Arial-BoldMT", 26, 100, 8, 12, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, "d", "Arial-BoldMT", 34, 100, 8, 12, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, " ", "Arial-BoldMT", 42, 100, 8, 12, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, "t", "Arial-BoldMT", 50, 100, 8, 12, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, "h", "Arial-BoldMT", 58, 100, 8, 12, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, "a", "Arial-BoldMT", 66, 100, 8, 12, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, "t", "Arial-BoldMT", 74, 100, 8, 12, false, false)
        );

        List<BoldTextDto> results = detector.detectBoldText(fragments);

        assertEquals(1, results.size());
        assertEquals("find that", results.get(0).text());
    }

    @Test
    void shouldPreserveTitleAndWordSpacingFromRealPdfStyleFragments() {
        List<PdfBoldTextDetector.PdfTextFragment> fragments = List.of(
                new PdfBoldTextDetector.PdfTextFragment(1, "D", "Arial-BoldMT", 120, 80, 16, 24, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, "a", "Arial-BoldMT", 140, 80, 16, 24, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, "y", "Arial-BoldMT", 156, 80, 16, 24, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, " ", "Arial-BoldMT", 172, 80, 8, 24, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, "o", "Arial-BoldMT", 180, 80, 16, 24, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, "f", "Arial-BoldMT", 196, 80, 12, 24, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, " ", "Arial-BoldMT", 208, 80, 8, 24, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, "t", "Arial-BoldMT", 216, 80, 12, 24, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, "h", "Arial-BoldMT", 231, 80, 14, 24, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, "e", "Arial-BoldMT", 245, 80, 12, 24, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, " ", "Arial-BoldMT", 257, 80, 8, 24, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, "D", "Arial-BoldMT", 265, 80, 16, 24, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, "e", "Arial-BoldMT", 281, 80, 16, 24, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, "a", "Arial-BoldMT", 297, 80, 16, 24, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, "d", "Arial-BoldMT", 313, 80, 16, 24, false, false),

                new PdfBoldTextDetector.PdfTextFragment(1, "c", "Arial-BoldMT", 20, 120, 12, 16, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, "e", "Arial-BoldMT", 32, 120, 12, 16, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, "m", "Arial-BoldMT", 44, 120, 12, 16, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, "e", "Arial-BoldMT", 56, 120, 12, 16, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, "t", "Arial-BoldMT", 68, 120, 12, 16, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, "e", "Arial-BoldMT", 80, 120, 12, 16, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, "r", "Arial-BoldMT", 92, 120, 12, 16, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, "y", "Arial-BoldMT", 104, 120, 12, 16, false, false),

                new PdfBoldTextDetector.PdfTextFragment(1, "a", "Arial-BoldMT", 260, 120, 12, 16, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, "t", "Arial-BoldMT", 272, 120, 12, 16, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, "m", "Arial-BoldMT", 284, 120, 12, 16, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, "o", "Arial-BoldMT", 296, 120, 12, 16, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, "s", "Arial-BoldMT", 308, 120, 12, 16, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, "p", "Arial-BoldMT", 320, 120, 12, 16, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, "h", "Arial-BoldMT", 332, 120, 12, 16, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, "e", "Arial-BoldMT", 344, 120, 12, 16, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, "r", "Arial-BoldMT", 356, 120, 12, 16, false, false),
                new PdfBoldTextDetector.PdfTextFragment(1, "e", "Arial-BoldMT", 368, 120, 12, 16, false, false)
        );

        List<BoldTextDto> results = detector.detectBoldText(fragments);
        assertEquals(2, results.size());
        assertEquals("Day of the Dead", results.get(0).text());
        assertEquals("cemetery atmosphere", results.get(1).text());
    }

    @Test
    void shouldRecognizeBoldFontNames() {
        PdfBoldTextDetector.PdfTextFragment boldFragment =
                new PdfBoldTextDetector.PdfTextFragment(1, "Present", "Arial-BoldMT", 10, 100, 80, 12, false, false);
        PdfBoldTextDetector.PdfTextFragment normalFragment =
                new PdfBoldTextDetector.PdfTextFragment(1, "normal", "Arial", 10, 120, 80, 12, false, false);

        assertTrue(detector.isBold(boldFragment));
        assertTrue(!detector.isBold(normalFragment));
    }

    @Test
    void shouldRecognizeBackgroundHighlightedText() {
        PdfBoldTextDetector.PdfTextFragment highlightedFragment =
                new PdfBoldTextDetector.PdfTextFragment(1, "going", "Arial", 20, 110, 40, 12, false, true);
        PdfBoldTextDetector.PdfTextFragment normalFragment =
                new PdfBoldTextDetector.PdfTextFragment(1, "ignored", "Arial", 120, 110, 50, 12, false, false);

        assertTrue(detector.isHighlight(highlightedFragment));
        assertTrue(!detector.isHighlight(normalFragment));
    }
}
