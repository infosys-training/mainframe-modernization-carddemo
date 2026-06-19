package com.carddemo.common.validation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Validation utilities migrated from CSLKPCDY.cpy (state codes, ZIP prefixes)
 * and CSUTLDTC.cbl (date validation).
 */
public final class CardDemoValidator {

    private static final Set<String> VALID_STATE_CODES = Set.of(
            "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA",
            "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD",
            "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ",
            "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC",
            "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY",
            "DC", "PR", "VI", "GU", "AS", "MP"
    );

    private static final Pattern SSN_PATTERN = Pattern.compile("^\\d{9}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\(?\\d{3}\\)?[\\s-]?\\d{3}[\\s-]?\\d{4}$");
    private static final Pattern ZIP_PATTERN = Pattern.compile("^\\d{5}(-\\d{4})?$");
    private static final Pattern ACCOUNT_ID_PATTERN = Pattern.compile("^\\d{11}$");
    private static final Pattern CARD_NUMBER_PATTERN = Pattern.compile("^\\d{16}$");

    private CardDemoValidator() {}

    public static boolean isValidStateCode(String stateCode) {
        return stateCode != null && VALID_STATE_CODES.contains(stateCode.toUpperCase().trim());
    }

    public static boolean isValidSsn(String ssn) {
        return ssn != null && SSN_PATTERN.matcher(ssn.trim()).matches();
    }

    public static boolean isValidPhoneNumber(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    public static boolean isValidZipCode(String zip) {
        return zip != null && ZIP_PATTERN.matcher(zip.trim()).matches();
    }

    public static boolean isValidAccountId(String accountId) {
        return accountId != null && ACCOUNT_ID_PATTERN.matcher(accountId.trim()).matches();
    }

    public static boolean isValidCardNumber(String cardNumber) {
        return cardNumber != null && CARD_NUMBER_PATTERN.matcher(cardNumber.trim()).matches();
    }

    public static boolean isValidDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return false;
        try {
            LocalDate.parse(dateStr.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean isValidDate(String dateStr, String pattern) {
        if (dateStr == null || dateStr.isBlank()) return false;
        try {
            LocalDate.parse(dateStr.trim(), DateTimeFormatter.ofPattern(pattern));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean isValidFicoScore(int score) {
        return score >= 300 && score <= 850;
    }
}
