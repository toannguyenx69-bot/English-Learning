package com.example.englishlearning.service;

import com.example.englishlearning.dto.BoldTextDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class PdfBoldTextDetector {

    private static final List<String> BOLD_KEYWORDS = List.of(
            "bold",
            "-bold",
            "black",
            "heavy",
            "demi"
    );

    private static final List<String> ITALIC_KEYWORDS = List.of(
            "italic",
            "oblique",
            "slanted",
            "it"
    );

    public List<BoldTextDto> detectBoldText(List<PdfTextFragment> fragments) {
        if (fragments == null || fragments.isEmpty()) {
            return List.of();
        }

        List<PdfTextFragment> boldFragments = fragments.stream()
                .filter(fragment -> fragment != null && fragment.text() != null)
                .filter(this::isBold)
                .toList();

        if (boldFragments.isEmpty()) {
            return List.of();
        }

        List<BdfPhrase> phrases = new ArrayList<>();
        List<PdfTextFragment> sorted = new ArrayList<>(boldFragments);
        sorted.sort(Comparator.comparingInt(PdfTextFragment::page)
                .thenComparingDouble(PdfTextFragment::y)
                .thenComparingDouble(PdfTextFragment::x));

        List<List<PdfTextFragment>> groupedLines = new ArrayList<>();
        List<PdfTextFragment> currentLine = new ArrayList<>();
        Integer lastPage = null;
        Double lastY = null;

        for (PdfTextFragment fragment : sorted) {
            if (fragment == null || fragment.text() == null) {
                continue;
            }

            if (currentLine.isEmpty()) {
                currentLine.add(fragment);
                lastPage = fragment.page();
                lastY = fragment.y();
                continue;
            }

            boolean samePage = lastPage != null && lastPage.equals(fragment.page());
            boolean sameLine = samePage && lastY != null && Math.abs(fragment.y() - lastY) <= 4.0;

            if (sameLine) {
                currentLine.add(fragment);
            } else {
                groupedLines.add(currentLine);
                currentLine = new ArrayList<>();
                currentLine.add(fragment);
                lastPage = fragment.page();
                lastY = fragment.y();
            }
        }

        if (!currentLine.isEmpty()) {
            groupedLines.add(currentLine);
        }

        for (List<PdfTextFragment> lineFragments : groupedLines) {
            if (lineFragments.isEmpty()) {
                continue;
            }

            BdfPhrase current = new BdfPhrase(
                    lineFragments.get(0).page(),
                    new StringBuilder(),
                    lineFragments.get(0).x(),
                    lineFragments.get(0).y(),
                    lineFragments.get(0).x() + lineFragments.get(0).width(),
                    lineFragments.get(0).y() + lineFragments.get(0).height()
            );

            Double lastXEnd = null;
            Double lastWidth = null;
            boolean pendingSpace = false;

            for (PdfTextFragment fragment : lineFragments) {
                String text = fragment.text();
                if (text == null) {
                    continue;
                }

                String token = text.trim();
                if (token.isEmpty()) {
                    if (current.builder.length() > 0 && !Character.isWhitespace(current.builder.charAt(current.builder.length() - 1))) {
                        pendingSpace = true;
                    }
                    continue;
                }

                if (current.builder.length() > 0) {
                    double gap = lastXEnd == null ? 0.0 : fragment.x() - lastXEnd;
                    if (pendingSpace || gap > Math.max(8.0, lastWidth == null ? 8.0 : lastWidth * 1.1)) {
                        if (!Character.isWhitespace(current.builder.charAt(current.builder.length() - 1))) {
                            current.builder.append(' ');
                        }
                    }
                }

                current.builder.append(token);
                pendingSpace = false;
                lastXEnd = fragment.x() + fragment.width();
                lastWidth = fragment.width();
                current.minX = Math.min(current.minX, fragment.x());
                current.maxX = Math.max(current.maxX, fragment.x() + fragment.width());
                current.minY = Math.min(current.minY, fragment.y());
                current.maxY = Math.max(current.maxY, fragment.y() + fragment.height());
            }

            String cleaned = current.builder.toString().replaceAll("\\s+", " ").trim();
            if (!cleaned.isEmpty()) {
                phrases.add(current);
            }
        }

        List<BoldTextDto> detected = new ArrayList<>();
        for (BdfPhrase phrase : phrases) {
            String cleaned = phrase.builder.toString().replaceAll("\\s+", " ").trim();
            if (cleaned.isEmpty()) {
                continue;
            }

            detected.add(new BoldTextDto(
                    phrase.page,
                    cleaned,
                    phrase.minX,
                    phrase.minY,
                    Math.max(0.0, phrase.maxX - phrase.minX),
                    Math.max(0.0, phrase.maxY - phrase.minY)
            ));
        }

        return detected;
    }

    public List<BoldTextDto> detectItalicText(List<PdfTextFragment> fragments) {
        if (fragments == null || fragments.isEmpty()) {
            return List.of();
        }

        List<PdfTextFragment> italicFragments = fragments.stream()
                .filter(fragment -> fragment != null && fragment.text() != null)
                .filter(this::isItalic)
                .toList();

        return buildPhraseResult(italicFragments);
    }

    public List<BoldTextDto> detectHighlightText(List<PdfTextFragment> fragments) {
        if (fragments == null || fragments.isEmpty()) {
            return List.of();
        }

        List<PdfTextFragment> highlightFragments = fragments.stream()
                .filter(fragment -> fragment != null && fragment.text() != null)
                .filter(this::isHighlight)
                .toList();

        return buildPhraseResult(highlightFragments);
    }

    public boolean isBold(PdfTextFragment fragment) {
        if (fragment == null || fragment.fontName() == null) {
            return false;
        }

        String normalized = fragment.fontName().toLowerCase();
        for (String keyword : BOLD_KEYWORDS) {
            if (normalized.contains(keyword)) {
                return true;
            }
        }

        return false;
    }

    public boolean isItalic(PdfTextFragment fragment) {
        if (fragment == null) {
            return false;
        }

        if (fragment.fontName() != null) {
            String normalized = fragment.fontName().toLowerCase();
            for (String keyword : ITALIC_KEYWORDS) {
                if (normalized.contains(keyword)) {
                    return true;
                }
            }
        }

        return fragment.isItalic();
    }

    public boolean isHighlight(PdfTextFragment fragment) {
        if (fragment == null || fragment.text() == null || fragment.text().isBlank()) {
            return false;
        }

        if (fragment.hasColor()) {
            return true;
        }

        String normalized = fragment.fontName() == null ? "" : fragment.fontName().toLowerCase();
        return normalized.contains("highlight")
                || normalized.contains("marker")
                || normalized.contains("yellow")
                || normalized.contains("bg");
    }

    private List<BoldTextDto> buildPhraseResult(List<PdfTextFragment> fragments) {
        if (fragments.isEmpty()) {
            return List.of();
        }

        List<PdfTextFragment> sorted = new ArrayList<>(fragments);
        sorted.sort(Comparator.comparingInt(PdfTextFragment::page)
                .thenComparingDouble(PdfTextFragment::y)
                .thenComparingDouble(PdfTextFragment::x));

        List<BdfPhrase> phrases = new ArrayList<>();
        List<List<PdfTextFragment>> groupedLines = new ArrayList<>();
        List<PdfTextFragment> currentLine = new ArrayList<>();
        Integer lastPage = null;
        Double lastY = null;

        for (PdfTextFragment fragment : sorted) {
            if (currentLine.isEmpty()) {
                currentLine.add(fragment);
                lastPage = fragment.page();
                lastY = fragment.y();
                continue;
            }

            boolean samePage = lastPage != null && lastPage.equals(fragment.page());
            boolean sameLine = samePage && lastY != null && Math.abs(fragment.y() - lastY) <= 4.0;

            if (sameLine) {
                currentLine.add(fragment);
            } else {
                groupedLines.add(currentLine);
                currentLine = new ArrayList<>();
                currentLine.add(fragment);
                lastPage = fragment.page();
                lastY = fragment.y();
            }
        }

        if (!currentLine.isEmpty()) {
            groupedLines.add(currentLine);
        }

        for (List<PdfTextFragment> lineFragments : groupedLines) {
            if (lineFragments.isEmpty()) {
                continue;
            }

            BdfPhrase current = new BdfPhrase(
                    lineFragments.get(0).page(),
                    new StringBuilder(),
                    lineFragments.get(0).x(),
                    lineFragments.get(0).y(),
                    lineFragments.get(0).x() + lineFragments.get(0).width(),
                    lineFragments.get(0).y() + lineFragments.get(0).height()
            );

            Double lastXEnd = null;
            Double lastWidth = null;
            boolean pendingSpace = false;

            for (PdfTextFragment fragment : lineFragments) {
                String text = fragment.text();
                if (text == null) {
                    continue;
                }

                String token = text.trim();
                if (token.isEmpty()) {
                    if (current.builder.length() > 0 && !Character.isWhitespace(current.builder.charAt(current.builder.length() - 1))) {
                        pendingSpace = true;
                    }
                    continue;
                }

                if (current.builder.length() > 0) {
                    double gap = lastXEnd == null ? 0.0 : fragment.x() - lastXEnd;
                    if (pendingSpace || gap > Math.max(8.0, lastWidth == null ? 8.0 : lastWidth * 1.1)) {
                        if (!Character.isWhitespace(current.builder.charAt(current.builder.length() - 1))) {
                            current.builder.append(' ');
                        }
                    }
                }

                current.builder.append(token);
                pendingSpace = false;
                lastXEnd = fragment.x() + fragment.width();
                lastWidth = fragment.width();
                current.minX = Math.min(current.minX, fragment.x());
                current.maxX = Math.max(current.maxX, fragment.x() + fragment.width());
                current.minY = Math.min(current.minY, fragment.y());
                current.maxY = Math.max(current.maxY, fragment.y() + fragment.height());
            }

            String cleaned = current.builder.toString().replaceAll("\\s+", " ").trim();
            if (!cleaned.isEmpty()) {
                phrases.add(current);
            }
        }

        List<BoldTextDto> detected = new ArrayList<>();
        for (BdfPhrase phrase : phrases) {
            String cleaned = phrase.builder.toString().replaceAll("\\s+", " ").trim();
            if (cleaned.isEmpty()) {
                continue;
            }

            detected.add(new BoldTextDto(
                    phrase.page,
                    cleaned,
                    phrase.minX,
                    phrase.minY,
                    Math.max(0.0, phrase.maxX - phrase.minX),
                    Math.max(0.0, phrase.maxY - phrase.minY)
            ));
        }

        return detected;
    }

    public record PdfTextFragment(
            Integer page,
            String text,
            String fontName,
            double x,
            double y,
            double width,
            double height,
            boolean isItalic,
            boolean hasColor
    ) {
    }

    private static final class BdfPhrase {
        private final Integer page;
        private final StringBuilder builder;
        private Double lastXEnd;
        private Double lastY;
        private boolean pendingSpace;
        private Double minX;
        private Double minY;
        private Double maxX;
        private Double maxY;

        private BdfPhrase(Integer page, StringBuilder builder, double x, double y, double maxX, double maxY) {
            this.page = page;
            this.builder = builder;
            this.lastXEnd = x;
            this.lastY = y;
            this.minX = x;
            this.minY = y;
            this.maxX = maxX;
            this.maxY = maxY;
        }
    }
}
