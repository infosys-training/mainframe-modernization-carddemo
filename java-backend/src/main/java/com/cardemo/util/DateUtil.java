package com.cardemo.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Date utility - migrated from COBOL program CSUTLDTC.cbl.
 *
 * Original COBOL program was a date validation subroutine called via
 * CICS LINK from programs like COACTUPC and COTRN02C.
 * It validated date formats and checked for valid calendar dates
 * (proper month/day ranges, leap year handling).
 *
 * Input: WS-DATE-TO-TEST (YYYY-MM-DD format)
 * Output: WS-DATE-RESULT ('0' = valid, '1' = invalid)
 */
public final class DateUtil {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private DateUtil() {
    }

    /**
     * Validate date in YYYY-MM-DD format.
     * Migrated from CSUTLDTC date validation logic.
     *
     * @param dateStr date string in YYYY-MM-DD format
     * @return true if valid date, false otherwise
     */
    public static boolean isValidDate(String dateStr) {
        if (dateStr == null || dateStr.length() != 10) {
            return false;
        }
        try {
            LocalDate.parse(dateStr, DATE_FORMAT);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Parse date string to LocalDate.
     *
     * @param dateStr date string in YYYY-MM-DD format
     * @return parsed LocalDate
     * @throws DateTimeParseException if format is invalid
     */
    public static LocalDate parseDate(String dateStr) {
        return LocalDate.parse(dateStr, DATE_FORMAT);
    }

    /**
     * Format LocalDate to string.
     *
     * @param date the date
     * @return formatted string in YYYY-MM-DD format
     */
    public static String formatDate(LocalDate date) {
        return date.format(DATE_FORMAT);
    }
}
