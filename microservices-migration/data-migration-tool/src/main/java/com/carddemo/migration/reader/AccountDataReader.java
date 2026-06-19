package com.carddemo.migration.reader;

import com.carddemo.common.entity.Account;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import static com.carddemo.migration.reader.CobolRecordParser.*;

/**
 * Reads account data from ASCII fixed-length files (CVACT01Y layout, 300-byte records).
 * Maps COBOL ACCOUNT-RECORD to Account entity.
 */
public class AccountDataReader {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    public List<Account> readAccounts(Reader reader) throws IOException {
        List<Account> accounts = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(reader)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                Account account = parseLine(line);
                if (account != null) {
                    accounts.add(account);
                }
            }
        }
        return accounts;
    }

    private Account parseLine(String line) {
        // CVACT01Y layout: ACCT-ID PIC 9(11) bytes 1-11
        String acctId = extract(line, 1, 11).trim();
        if (acctId.isEmpty() || "00000000000".equals(acctId)) return null;

        Account a = new Account(acctId);
        a.setActiveStatus(extract(line, 12, 1));
        a.setCurrentBalance(parseSignedDecimal(extract(line, 13, 12), 2));
        a.setCreditLimit(parseSignedDecimal(extract(line, 25, 12), 2));
        a.setCashCreditLimit(parseSignedDecimal(extract(line, 37, 12), 2));
        a.setOpenDate(parseDate(extract(line, 49, 10)));
        a.setExpirationDate(parseDate(extract(line, 59, 10)));
        a.setReissueDate(parseDate(extract(line, 69, 10)));
        a.setCurrentCycleCredit(parseSignedDecimal(extract(line, 79, 12), 2));
        a.setCurrentCycleDebit(parseSignedDecimal(extract(line, 91, 12), 2));
        a.setAddressZip(extract(line, 103, 10).trim());
        a.setGroupId(extract(line, 113, 10).trim());

        return a;
    }

    private LocalDate parseDate(String raw) {
        if (raw == null || raw.isBlank()) return null;
        try {
            return LocalDate.parse(raw.trim(), DATE_FMT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
