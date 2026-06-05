# DATA DICTIONARY — CardDemo COBOL Estate

## Summary

| Metric | Value |
|--------|-------|
| Total Copybooks | 28 |
| Business Entity Copybooks | 12 |
| UI/Screen Copybooks | 10 |
| Shared Utility Copybooks | 6 |
| Business Entities Identified | 8 (Account, Customer, Card, Transaction, XREF, Disclosure Group, Transaction Type, Transaction Category) |

---

## 1. Account Entity

### 1.1 CVACT01Y.cpy — Account Record (RECLN 300)

| Level | Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation |
|-------|-----------|------------|-----------|-------|-----------------|------------|
| 01 | ACCOUNT-RECORD | — | Group | 300 | Account master record | — |
| 05 | ACCT-ID | PIC 9(11) | Numeric display | 11 | Primary key — unique account identifier | Must be 11-digit numeric |
| 05 | ACCT-ACTIVE-STATUS | PIC X(01) | Alphanumeric | 1 | Account status flag | 'Y' = active (inferred) |
| 05 | ACCT-CURR-BAL | PIC S9(10)V99 | Signed decimal, implied V | 12 | Current account balance | Signed; 2 decimal places |
| 05 | ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal, implied V | 12 | Maximum credit limit | Signed; 2 decimal places |
| 05 | ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal, implied V | 12 | Cash advance credit limit | Signed; 2 decimal places |
| 05 | ACCT-OPEN-DATE | PIC X(10) | Alphanumeric | 10 | Date account was opened | YYYY-MM-DD format |
| 05 | ACCT-EXPIRAION-DATE | PIC X(10) | Alphanumeric | 10 | Account expiration date | YYYY-MM-DD format |
| 05 | ACCT-REISSUE-DATE | PIC X(10) | Alphanumeric | 10 | Last reissue date | YYYY-MM-DD format |
| 05 | ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Signed decimal, implied V | 12 | Current cycle credit (payments) | Signed; 2 decimal places |
| 05 | ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 | Signed decimal, implied V | 12 | Current cycle debit (charges) | Signed; 2 decimal places |
| 05 | ACCT-ADDR-ZIP | PIC X(10) | Alphanumeric | 10 | Account holder ZIP code | Validated against CSLKPCDY |
| 05 | ACCT-GROUP-ID | PIC X(10) | Alphanumeric | 10 | Disclosure group classification | Used to look up interest rates |
| 05 | FILLER | PIC X(178) | Filler | 178 | Padding to 300 bytes | — |

**VSAM Key:** ACCT-ID (offset 0, length 11)
**Programs using:** CBACT01C, CBACT04C, CBSTM03A/B, CBTRN01C, CBTRN02C, COACTUPC, COACTVWC, COBIL00C, COTRN02C

---

## 2. Customer Entity

### 2.1 CVCUS01Y.cpy — Customer Record (RECLN 500)

| Level | Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation |
|-------|-----------|------------|-----------|-------|-----------------|------------|
| 01 | CUSTOMER-RECORD | — | Group | 500 | Customer master record | — |
| 05 | CUST-ID | PIC 9(09) | Numeric display | 9 | Primary key — unique customer ID | Must be 9-digit numeric |
| 05 | CUST-FIRST-NAME | PIC X(25) | Alphanumeric | 25 | Customer first name | — |
| 05 | CUST-MIDDLE-NAME | PIC X(25) | Alphanumeric | 25 | Customer middle name | — |
| 05 | CUST-LAST-NAME | PIC X(25) | Alphanumeric | 25 | Customer last name | — |
| 05 | CUST-ADDR-LINE-1 | PIC X(50) | Alphanumeric | 50 | Address line 1 | — |
| 05 | CUST-ADDR-LINE-2 | PIC X(50) | Alphanumeric | 50 | Address line 2 | — |
| 05 | CUST-ADDR-LINE-3 | PIC X(50) | Alphanumeric | 50 | Address line 3 | — |
| 05 | CUST-ADDR-STATE-CD | PIC X(02) | Alphanumeric | 2 | US state code | Validated against 50-state list in CSLKPCDY |
| 05 | CUST-ADDR-COUNTRY-CD | PIC X(03) | Alphanumeric | 3 | Country code | — |
| 05 | CUST-ADDR-ZIP | PIC X(10) | Alphanumeric | 10 | ZIP code | Validated against state-to-ZIP-prefix table in CSLKPCDY |
| 05 | CUST-PHONE-NUM-1 | PIC X(15) | Alphanumeric | 15 | Primary phone number | Area code validated against NANPA list in CSLKPCDY |
| 05 | CUST-PHONE-NUM-2 | PIC X(15) | Alphanumeric | 15 | Secondary phone number | Area code validated against NANPA list in CSLKPCDY |
| 05 | CUST-SSN | PIC 9(09) | Numeric display | 9 | Social Security Number | Must be 9-digit numeric |
| 05 | CUST-GOVT-ISSUED-ID | PIC X(20) | Alphanumeric | 20 | Government-issued ID | — |
| 05 | CUST-DOB-YYYY-MM-DD | PIC X(10) | Alphanumeric | 10 | Date of birth | YYYY-MM-DD; must not be in the future (CSUTLDPY) |
| 05 | CUST-EFT-ACCOUNT-ID | PIC X(10) | Alphanumeric | 10 | EFT / electronic transfer account | — |
| 05 | CUST-PRI-CARD-HOLDER-IND | PIC X(01) | Alphanumeric | 1 | Primary cardholder indicator | 'Y'/'N' (inferred) |
| 05 | CUST-FICO-CREDIT-SCORE | PIC 9(03) | Numeric display | 3 | FICO credit score | 0–999 range |
| 05 | FILLER | PIC X(168) | Filler | 168 | Padding to 500 bytes | — |

### 2.2 CUSTREC.cpy — Alternate Customer Layout (Statement Generation)

Identical field layout to CVCUS01Y.cpy. Used by CBSTM03A for statement generation. Minor difference: DOB field named `CUST-DOB-YYYYMMDD` (no hyphens).

**VSAM Key:** CUST-ID (offset 0, length 9)
**Programs using:** CBCUS01C, CBSTM03A/B, CBTRN01C, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC

---

## 3. Card Entity

### 3.1 CVACT02Y.cpy — Card Record (RECLN 150)

| Level | Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation |
|-------|-----------|------------|-----------|-------|-----------------|------------|
| 01 | CARD-RECORD | — | Group | 150 | Credit card master record | — |
| 05 | CARD-NUM | PIC X(16) | Alphanumeric | 16 | Card number (primary key in CARDDATA VSAM) | 16-character card number |
| 05 | CARD-ACCT-ID | PIC 9(11) | Numeric display | 11 | Foreign key → ACCT-ID | Links card to account |
| 05 | CARD-CVV-CD | PIC 9(03) | Numeric display | 3 | Card verification value (CVV) | 3-digit numeric |
| 05 | CARD-EMBOSSED-NAME | PIC X(50) | Alphanumeric | 50 | Name printed on the card | — |
| 05 | CARD-EXPIRAION-DATE | PIC X(10) | Alphanumeric | 10 | Card expiration date | YYYY-MM-DD format |
| 05 | CARD-ACTIVE-STATUS | PIC X(01) | Alphanumeric | 1 | Card active status flag | 'Y' = active (inferred) |
| 05 | FILLER | PIC X(59) | Filler | 59 | Padding to 150 bytes | — |

**VSAM Key:** CARD-NUM (offset 0, length 16)
**AIX:** Alternate index on CARD-ACCT-ID (defined by CARDFILE.jcl)
**Programs using:** CBACT02C, CBTRN01C, COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC

---

## 4. Card–Account Cross-Reference

### 4.1 CVACT03Y.cpy — Card XREF Record (RECLN 50)

| Level | Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation |
|-------|-----------|------------|-----------|-------|-----------------|------------|
| 01 | CARD-XREF-RECORD | — | Group | 50 | Card-to-account/customer cross-reference | — |
| 05 | XREF-CARD-NUM | PIC X(16) | Alphanumeric | 16 | Card number (primary key) | FK → CARD-NUM |
| 05 | XREF-CUST-ID | PIC 9(09) | Numeric display | 9 | Customer ID | FK → CUST-ID |
| 05 | XREF-ACCT-ID | PIC 9(11) | Numeric display | 11 | Account ID | FK → ACCT-ID |
| 05 | FILLER | PIC X(14) | Filler | 14 | Padding to 50 bytes | — |

**VSAM Key:** XREF-CARD-NUM (offset 0, length 16)
**AIX:** Alternate index on XREF-ACCT-ID (defined by XREFFILE.jcl)
**Programs using:** CBACT04C, CBSTM03A/B, CBTRN01C, CBTRN02C, CBTRN03C, COACTUPC, COACTVWC, COBIL00C, COTRN02C

---

## 5. Transaction Entity

### 5.1 CVTRA05Y.cpy — Transaction Record (RECLN 350)

| Level | Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation |
|-------|-----------|------------|-----------|-------|-----------------|------------|
| 01 | TRAN-RECORD | — | Group | 350 | Transaction master record | — |
| 05 | TRAN-ID | PIC X(16) | Alphanumeric | 16 | Unique transaction identifier (primary key) | System-generated sequence |
| 05 | TRAN-TYPE-CD | PIC X(02) | Alphanumeric | 2 | Transaction type code | FK → TRAN-TYPE in CVTRA03Y |
| 05 | TRAN-CAT-CD | PIC 9(04) | Numeric display | 4 | Transaction category code | FK → TRAN-CAT-CD in CVTRA04Y |
| 05 | TRAN-SOURCE | PIC X(10) | Alphanumeric | 10 | Source of the transaction | — |
| 05 | TRAN-DESC | PIC X(100) | Alphanumeric | 100 | Transaction description | — |
| 05 | TRAN-AMT | PIC S9(09)V99 | Signed decimal, implied V | 11 | Transaction amount | Signed; 2 decimal places |
| 05 | TRAN-MERCHANT-ID | PIC 9(09) | Numeric display | 9 | Merchant identifier | — |
| 05 | TRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric | 50 | Merchant name | — |
| 05 | TRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric | 50 | Merchant city | — |
| 05 | TRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric | 10 | Merchant ZIP code | — |
| 05 | TRAN-CARD-NUM | PIC X(16) | Alphanumeric | 16 | Card number used for this transaction | FK → CARD-NUM |
| 05 | TRAN-ORIG-TS | PIC X(26) | Alphanumeric | 26 | Original transaction timestamp | YYYY-MM-DD-HH.MM.SS.MMMMMM |
| 05 | TRAN-PROC-TS | PIC X(26) | Alphanumeric | 26 | Processed timestamp | YYYY-MM-DD-HH.MM.SS.MMMMMM |
| 05 | FILLER | PIC X(20) | Filler | 20 | Padding to 350 bytes | — |

### 5.2 CVTRA06Y.cpy — Daily Transaction Record (RECLN 350)

Identical layout to CVTRA05Y but with `DALYTRAN-` prefix on all fields. Used as the input format for the daily transaction feed before posting to the master file.

### 5.3 COSTM01.CPY — Transaction Altered Layout (Statement Reporting)

| Level | Field Name | PIC Clause | Data Type | Bytes | Business Meaning |
|-------|-----------|------------|-----------|-------|-----------------|
| 01 | TRNX-RECORD | — | Group | 350 | Rekeyed transaction for statement reporting |
| 05 | TRNX-KEY | — | Group | 32 | Compound key: card-num + tran-id |
| 10 | TRNX-CARD-NUM | PIC X(16) | Alphanumeric | 16 | Card number |
| 10 | TRNX-ID | PIC X(16) | Alphanumeric | 16 | Transaction ID |
| 05 | TRNX-REST | — | Group | 318 | Remaining transaction fields |
| 10 | TRNX-TYPE-CD | PIC X(02) | Alphanumeric | 2 | Transaction type code |
| 10 | TRNX-CAT-CD | PIC 9(04) | Numeric | 4 | Transaction category code |

**VSAM Key:** TRAN-ID (offset 0, length 16)
**AIX:** Alternate index on TRAN-PROC-TS (offset 304, length 26) — defined by TRANFILE.jcl / TRANIDX.jcl
**Programs using:** CBACT04C, CBSTM03A, CBTRN01C, CBTRN02C, CBTRN03C, COTRN00C, COTRN01C, COTRN02C, COBIL00C, CORPT00C

---

## 6. Transaction Category Balance

### 6.1 CVTRA01Y.cpy — Transaction Category Balance Record (RECLN 50)

| Level | Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation |
|-------|-----------|------------|-----------|-------|-----------------|------------|
| 01 | TRAN-CAT-BAL-RECORD | — | Group | 50 | Running balance per account per transaction category | — |
| 05 | TRAN-CAT-KEY | — | Group | 17 | Composite primary key | — |
| 10 | TRANCAT-ACCT-ID | PIC 9(11) | Numeric display | 11 | Account ID | FK → ACCT-ID |
| 10 | TRANCAT-TYPE-CD | PIC X(02) | Alphanumeric | 2 | Transaction type code | — |
| 10 | TRANCAT-CD | PIC 9(04) | Numeric display | 4 | Transaction category code | — |
| 05 | TRAN-CAT-BAL | PIC S9(09)V99 | Signed decimal, implied V | 11 | Balance for this category | — |
| 05 | FILLER | PIC X(22) | Filler | 22 | Padding to 50 bytes | — |

**VSAM Key:** TRAN-CAT-KEY (offset 0, length 17)
**Programs using:** CBACT04C, CBTRN02C

---

## 7. Disclosure Group (Interest Rate Lookup)

### 7.1 CVTRA02Y.cpy — Disclosure Group Record (RECLN 50)

| Level | Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation |
|-------|-----------|------------|-----------|-------|-----------------|------------|
| 01 | DIS-GROUP-RECORD | — | Group | 50 | Interest rate schedule per group/type/category | — |
| 05 | DIS-GROUP-KEY | — | Group | 16 | Composite primary key | — |
| 10 | DIS-ACCT-GROUP-ID | PIC X(10) | Alphanumeric | 10 | Account group identifier | FK → ACCT-GROUP-ID |
| 10 | DIS-TRAN-TYPE-CD | PIC X(02) | Alphanumeric | 2 | Transaction type code | — |
| 10 | DIS-TRAN-CAT-CD | PIC 9(04) | Numeric display | 4 | Transaction category code | — |
| 05 | DIS-INT-RATE | PIC S9(04)V99 | Signed decimal, implied V | 6 | Interest rate for this category (annual %) | — |
| 05 | FILLER | PIC X(28) | Filler | 28 | Padding to 50 bytes | — |

**VSAM Key:** DIS-GROUP-KEY (offset 0, length 16)
**Programs using:** CBACT04C

---

## 8. Transaction Type & Category Reference

### 8.1 CVTRA03Y.cpy — Transaction Type Record (RECLN 60)

| Level | Field Name | PIC Clause | Data Type | Bytes | Business Meaning |
|-------|-----------|------------|-----------|-------|-----------------|
| 01 | TRAN-TYPE-RECORD | — | Group | 60 | Transaction type lookup |
| 05 | TRAN-TYPE | PIC X(02) | Alphanumeric | 2 | Type code (primary key) |
| 05 | TRAN-TYPE-DESC | PIC X(50) | Alphanumeric | 50 | Type description (e.g., "Purchase", "Cash Advance") |
| 05 | FILLER | PIC X(08) | Filler | 8 | Padding to 60 bytes |

### 8.2 CVTRA04Y.cpy — Transaction Category Record (RECLN 60)

| Level | Field Name | PIC Clause | Data Type | Bytes | Business Meaning |
|-------|-----------|------------|-----------|-------|-----------------|
| 01 | TRAN-CAT-RECORD | — | Group | 60 | Transaction category lookup |
| 05 | TRAN-CAT-KEY | — | Group | 6 | Composite key |
| 10 | TRAN-TYPE-CD | PIC X(02) | Alphanumeric | 2 | Parent type code |
| 10 | TRAN-CAT-CD | PIC 9(04) | Numeric | 4 | Category code within type |
| 05 | TRAN-CAT-TYPE-DESC | PIC X(50) | Alphanumeric | 50 | Category description |
| 05 | FILLER | PIC X(04) | Filler | 4 | Padding to 60 bytes |

**Programs using:** CBTRN03C (report joins type + category descriptions)

---

## 9. User Security

### 9.1 CSUSR01Y.cpy — User Security Record (RECLN 80)

| Level | Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation |
|-------|-----------|------------|-----------|-------|-----------------|------------|
| 01 | SEC-USER-DATA | — | Group | 80 | User credentials record | — |
| 05 | SEC-USR-ID | PIC X(08) | Alphanumeric | 8 | User ID (primary key) | Unique user identifier |
| 05 | SEC-USR-FNAME | PIC X(20) | Alphanumeric | 20 | User first name | — |
| 05 | SEC-USR-LNAME | PIC X(20) | Alphanumeric | 20 | User last name | — |
| 05 | SEC-USR-PWD | PIC X(08) | Alphanumeric | 8 | Password (plaintext) | — |
| 05 | SEC-USR-TYPE | PIC X(01) | Alphanumeric | 1 | User type: 'A'=Admin, 'U'=Regular | 88-level in COCOM01Y |
| 05 | SEC-USR-FILLER | PIC X(23) | Filler | 23 | Padding to 80 bytes | — |

**VSAM Key:** SEC-USR-ID (offset 0, length 8)
**Programs using:** COSGN00C, COUSR00C, COUSR01C, COUSR02C, COUSR03C

---

## 10. Inter-Program Communication

### 10.1 COCOM01Y.cpy — COMMAREA (CICS inter-program area)

| Level | Field Name | PIC Clause | Data Type | Bytes | Business Meaning |
|-------|-----------|------------|-----------|-------|-----------------|
| 01 | CARDDEMO-COMMAREA | — | Group | ~100 | Shared state passed via CICS COMMAREA |
| 05 | CDEMO-GENERAL-INFO | — | Group | — | Navigation and session state |
| 10 | CDEMO-FROM-TRANID | PIC X(04) | Alphanumeric | 4 | Source transaction ID |
| 10 | CDEMO-FROM-PROGRAM | PIC X(08) | Alphanumeric | 8 | Source program name |
| 10 | CDEMO-TO-TRANID | PIC X(04) | Alphanumeric | 4 | Target transaction ID |
| 10 | CDEMO-TO-PROGRAM | PIC X(08) | Alphanumeric | 8 | Target program name |
| 10 | CDEMO-USER-ID | PIC X(08) | Alphanumeric | 8 | Logged-in user ID |
| 10 | CDEMO-USER-TYPE | PIC X(01) | Alphanumeric | 1 | 88 CDEMO-USRTYP-ADMIN='A', 88 CDEMO-USRTYP-USER='U' |
| 10 | CDEMO-PGM-CONTEXT | PIC 9(01) | Numeric | 1 | 88 CDEMO-PGM-ENTER=0, 88 CDEMO-PGM-REENTER=1 |
| 05 | CDEMO-CUSTOMER-INFO | — | Group | — | Customer context |
| 10 | CDEMO-CUST-ID | PIC 9(09) | Numeric | 9 | Selected customer ID |
| 10 | CDEMO-CUST-FNAME | PIC X(25) | Alphanumeric | 25 | Customer first name |
| 10 | CDEMO-CUST-MNAME | PIC X(25) | Alphanumeric | 25 | Customer middle name |
| 10 | CDEMO-CUST-LNAME | PIC X(25) | Alphanumeric | 25 | Customer last name |
| 05 | CDEMO-ACCOUNT-INFO | — | Group | — | Account context |
| 10 | CDEMO-ACCT-ID | PIC 9(11) | Numeric | 11 | Selected account ID |
| 10 | CDEMO-ACCT-STATUS | PIC X(01) | Alphanumeric | 1 | Account status |
| 05 | CDEMO-CARD-INFO | — | Group | — | Card context |
| 10 | CDEMO-CARD-NUM | PIC 9(16) | Numeric | 16 | Selected card number |
| 05 | CDEMO-MORE-INFO | — | Group | — | Map tracking |
| 10 | CDEMO-LAST-MAP | PIC X(7) | Alphanumeric | 7 | Last displayed map |
| 10 | CDEMO-LAST-MAPSET | PIC X(7) | Alphanumeric | 7 | Last displayed mapset |

**Programs using:** All 18 CICS programs

---

## 11. Navigation Menu Data

### 11.1 COADM02Y.cpy — Admin Menu Options

Defines 4 admin menu entries routed via CICS XCTL:

| Option | Label | Target Program |
|--------|-------|---------------|
| 1 | User List (Security) | COUSR00C |
| 2 | User Add (Security) | COUSR01C |
| 3 | User Update (Security) | COUSR02C |
| 4 | User Delete (Security) | COUSR03C |

### 11.2 COMEN02Y.cpy — Main Menu Options

Defines 10 regular-user menu entries:

| Option | Label | Target Program |
|--------|-------|---------------|
| 1 | Account View | COACTVWC |
| 2 | Account Update | COACTUPC |
| 3 | Credit Card List | COCRDLIC |
| 4 | Credit Card View | COCRDSLC |
| 5 | Credit Card Update | COCRDUPC |
| 6 | Transaction List | COTRN00C |
| 7 | Transaction View | COTRN01C |
| 8 | Transaction Add | COTRN02C |
| 9 | Transaction Reports | CORPT00C |
| 10 | Bill Payment | COBIL00C |

---

## 12. Reporting Structures

### 12.1 CVTRA07Y.cpy — Transaction Report Layout (RECLN 133)

| Level | Field Name | PIC Clause | Bytes | Business Meaning |
|-------|-----------|------------|-------|-----------------|
| 01 | REPORT-NAME-HEADER | — | — | Report title block |
| 05 | REPT-SHORT-NAME | PIC X(38) | 38 | Value: 'DALYREPT' |
| 05 | REPT-LONG-NAME | PIC X(41) | 41 | Value: 'Daily Transaction Report' |
| 05 | REPT-DATE-HEADER | PIC X(12) | 12 | Value: 'Date Range: ' |
| 05 | REPT-START-DATE | PIC X(10) | 10 | Start date of report |
| 05 | REPT-END-DATE | PIC X(10) | 10 | End date of report |
| 01 | TRANSACTION-DETAIL-REPORT | — | 133 | Detail line per transaction |
| 05 | TRAN-REPORT-TRANS-ID | PIC X(16) | 16 | Transaction ID |
| 05 | TRAN-REPORT-ACCOUNT-ID | PIC X(11) | 11 | Account ID |
| 05 | TRAN-REPORT-TYPE-CD | PIC X(02) | 2 | Type code |
| 05 | TRAN-REPORT-TYPE-DESC | PIC X(15) | 15 | Type description |
| 05 | TRAN-REPORT-CAT-CD | PIC 9(04) | 4 | Category code |
| 05 | TRAN-REPORT-CAT-DESC | PIC X(29) | 29 | Category description |
| 05 | TRAN-REPORT-SOURCE | PIC X(10) | 10 | Source |
| 05 | TRAN-REPORT-AMT | PIC -ZZZ,ZZZ,ZZZ.ZZ | 16 | Formatted amount |
| 01 | REPORT-PAGE-TOTALS | — | — | Page subtotal line |
| 05 | REPT-PAGE-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | 16 | Page total |
| 01 | REPORT-ACCOUNT-TOTALS | — | — | Account subtotal line |
| 05 | REPT-ACCOUNT-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | 16 | Account total |
| 01 | REPORT-GRAND-TOTALS | — | — | Grand total line |
| 05 | REPT-GRAND-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | 16 | Grand total |

---

## 13. Shared Utility Copybooks

### 13.1 CSDAT01Y.cpy — Date/Time Working Storage

| Field | PIC Clause | Business Meaning |
|-------|------------|-----------------|
| WS-CURDATE (YYYY/MM/DD) | PIC 9(04)/9(02)/9(02) | Current date from CICS ASKTIME |
| WS-CURTIME (HH/MM/SS/MS) | PIC 9(02) each | Current time |
| WS-CURDATE-MM-DD-YY | PIC X(08) | Formatted MM/DD/YY display |
| WS-CURTIME-HH-MM-SS | PIC X(08) | Formatted HH:MM:SS display |
| WS-TIMESTAMP | PIC X(26) | YYYY-MM-DD HH:MM:SS.MMMMMM |

### 13.2 CSUTLDWY.cpy — Date Validation Working Storage

Provides comprehensive CCYYMMDD date editing fields:
- `WS-EDIT-DATE-CCYYMMDD` (group: CC + YY + MM + DD with REDEFINES to numeric)
- 88-level conditions: `THIS-CENTURY` (20), `LAST-CENTURY` (19), `WS-VALID-MONTH` (1–12), `WS-VALID-DAY` (1–31), `WS-31-DAY-MONTH`, `WS-FEBRUARY`, `WS-DAY-29/30/31`
- `WS-DATE-VALIDATION-RESULT`: severity, message code, and result text from LE date services

### 13.3 CSUTLDPY.cpy — Date Validation Procedure Division

Reusable PERFORM paragraphs:
- `EDIT-DATE-CCYYMMDD` — orchestrates full date validation
- `EDIT-YEAR-CCYY` — century must be 19 or 20; year must be numeric
- `EDIT-MONTH` — month 1–12
- `EDIT-DAY` — day 1–31 with month-specific rules (30-day months, February, leap year)
- `EDIT-DAY-MONTH-YEAR` — cross-field validation (no 31st in 30-day months, Feb 29 leap year check)
- `EDIT-DATE-LE` — final validation via CALL 'CSUTLDTC' (LE CEEDAYS service)
- `EDIT-DATE-OF-BIRTH` — ensures date is not in the future

### 13.4 CSLKPCDY.cpy — Lookup / Validation Code Tables

Contains comprehensive validation data compiled into 88-level conditions:
- **NANPA Phone Area Codes**: 300+ valid North American area codes (88 VALID-PHONE-AREA-CODE)
- **US State Codes**: All 50 states (88 VALID-US-STATE-CODE)
- **US State-to-ZIP Prefix Table**: Maps state codes to valid ZIP prefix ranges for cross-validation

### 13.5 CVCRD01Y.cpy — Card Working Areas (CICS)

Common CICS working storage for card-related programs:
- `CCARD-AID` — attention identifier (ENTER, CLEAR, PA1, PA2, PFK01–PFK12)
- `CCARD-NEXT-PROG` — next program for XCTL navigation
- `CCARD-NEXT-MAPSET` / `CCARD-NEXT-MAP` — next BMS map
- `CCARD-ERROR-MSG` / `CCARD-RETURN-MSG` — user-facing messages (75 chars)
- `CC-ACCT-ID`, `CC-CARD-NUM`, `CC-CUST-ID` — context identifiers with numeric REDEFINES

### 13.6 UNUSED1Y.cpy — Unused/Placeholder Record

80-byte record with generic fields (ID, FNAME, LNAME, PWD, TYPE). Not referenced by any program.

---

## 14. UI / Screen Copybooks (BMS Maps)

These copybooks define the BMS screen layouts (generated from `app/bms/` sources). Each online program has a corresponding map copybook:

| Copybook | BMS Map | Used By |
|----------|---------|---------|
| COSGN00 | Sign-on screen | COSGN00C |
| COADM01 | Admin menu | COADM01C |
| COMEN01 | Main menu | COMEN01C |
| COACTVW | Account view | COACTVWC |
| COACTUP | Account update | COACTUPC |
| COCRDLI | Credit card list | COCRDLIC |
| COCRDSL | Credit card detail | COCRDSLC |
| COCRDUP | Credit card update | COCRDUPC |
| COTRN00 | Transaction list | COTRN00C |
| COTRN01 | Transaction view | COTRN01C |
| COTRN02 | Transaction add | COTRN02C |
| COBIL00 | Bill payment | COBIL00C |
| CORPT00 | Report request | CORPT00C |
| COUSR00 | User list | COUSR00C |
| COUSR01 | User add | COUSR01C |
| COUSR02 | User update | COUSR02C |
| COUSR03 | User delete | COUSR03C |
| COTTL01Y | Common title line | All CICS programs |
| CSMSG01Y / CSMSG02Y | Common message lines | Most CICS programs |

Additional utility copybooks used for screen attribute manipulation:
- **CSSETATY** — SET ATTRIBUTE macro, used via COPY … REPLACING (39 times in COACTUPC alone)
- **CSSTRPFY** — String-to-packed conversion for screen field parsing
