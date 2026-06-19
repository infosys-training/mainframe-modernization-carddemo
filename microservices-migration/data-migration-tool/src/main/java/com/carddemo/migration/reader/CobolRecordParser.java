package com.carddemo.migration.reader;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Parses COBOL fixed-length records with overpunch encoding and implied decimals.
 * Handles PIC S9(n)V99 fields with trailing overpunch sign encoding.
 */
public final class CobolRecordParser {

    private CobolRecordParser() {}

    /**
     * Extracts a substring from a fixed-length record.
     * COBOL positions are 1-based; Java is 0-based.
     */
    public static String extract(String record, int start, int length) {
        int zeroStart = start - 1;
        int end = Math.min(zeroStart + length, record.length());
        if (zeroStart >= record.length()) return "";
        return record.substring(zeroStart, end);
    }

    /**
     * Parses a signed decimal field with trailing overpunch encoding.
     * PIC S9(n)V9(m) → n+m display bytes, scale = m digits after implied decimal.
     */
    public static BigDecimal parseSignedDecimal(String raw, int scale) {
        if (raw == null || raw.isBlank()) return BigDecimal.ZERO;

        String trimmed = raw.trim();
        if (trimmed.isEmpty()) return BigDecimal.ZERO;

        char lastChar = trimmed.charAt(trimmed.length() - 1);
        String prefix = trimmed.substring(0, trimmed.length() - 1);
        int digit;
        boolean negative;

        switch (lastChar) {
            case '{': digit = 0; negative = false; break;
            case 'A': digit = 1; negative = false; break;
            case 'B': digit = 2; negative = false; break;
            case 'C': digit = 3; negative = false; break;
            case 'D': digit = 4; negative = false; break;
            case 'E': digit = 5; negative = false; break;
            case 'F': digit = 6; negative = false; break;
            case 'G': digit = 7; negative = false; break;
            case 'H': digit = 8; negative = false; break;
            case 'I': digit = 9; negative = false; break;
            case '}': digit = 0; negative = true; break;
            case 'J': digit = 1; negative = true; break;
            case 'K': digit = 2; negative = true; break;
            case 'L': digit = 3; negative = true; break;
            case 'M': digit = 4; negative = true; break;
            case 'N': digit = 5; negative = true; break;
            case 'O': digit = 6; negative = true; break;
            case 'P': digit = 7; negative = true; break;
            case 'Q': digit = 8; negative = true; break;
            case 'R': digit = 9; negative = true; break;
            default:
                if (Character.isDigit(lastChar)) {
                    digit = Character.getNumericValue(lastChar);
                    negative = false;
                } else {
                    return BigDecimal.ZERO;
                }
        }

        String numStr = prefix + digit;
        BigDecimal value = new BigDecimal(numStr).movePointLeft(scale);
        return negative ? value.negate() : value;
    }

    /**
     * Parses an unsigned numeric field (PIC 9(n)).
     */
    public static long parseUnsignedNumeric(String raw) {
        if (raw == null || raw.isBlank()) return 0;
        try {
            return Long.parseLong(raw.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
