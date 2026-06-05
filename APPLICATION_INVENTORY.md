# APPLICATION INVENTORY — CardDemo COBOL Estate

## Summary

| Metric | Value |
|--------|-------|
| Total COBOL Programs | 28 |
| Online (CICS) Programs | 18 |
| Batch Programs | 9 |
| Batch Subroutines | 1 |
| Total Lines of Code | 19,254 |
| JCL Jobs | 29 |
| Copybooks | 28 |

---

## 1. COBOL Program Catalog

### 1.1 Batch Programs

| # | Filename | LOC | Purpose | Key I/O (Files Read) | Key I/O (Files Written) | Copybooks Referenced |
|---|----------|-----|---------|----------------------|------------------------|---------------------|
| 1 | CBACT01C.cbl | 193 | Read and print account data file | ACCTFILE (VSAM KSDS) | SYSOUT (display) | CVACT01Y |
| 2 | CBACT02C.cbl | 178 | Read and print card data file | CARDFILE (VSAM KSDS) | SYSOUT (display) | CVACT02Y |
| 3 | CBACT03C.cbl | 178 | Read and print account cross-reference file | XREFFILE (VSAM KSDS) | SYSOUT (display) | CVACT03Y |
| 4 | CBACT04C.cbl | 652 | Interest calculator — compute interest/fees on account balances | TCATBALF (VSAM KSDS), XREFFILE (VSAM KSDS), DISCGRP (VSAM KSDS), TRANSACT (sequential) | ACCTFILE (VSAM KSDS, rewrite), TRANSACT (sequential, write) | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |
| 5 | CBCUS01C.cbl | 178 | Read and print customer data file | CUSTFILE (VSAM KSDS) | SYSOUT (display) | CVCUS01Y |
| 6 | CBSTM03A.CBL | 924 | Generate account statements (text + HTML) from transaction data | (via CBSTM03B): TRNXFILE, XREFFILE, CUSTFILE, ACCTFILE | STMTFILE (text), HTMLFILE (HTML) | COSTM01, CVACT03Y, CUSTREC, CVACT01Y |
| 7 | CBSTM03B.CBL | 230 | Subroutine — file I/O processing for statement generation | TRNXFILE (VSAM KSDS), XREFFILE (VSAM KSDS), CUSTFILE (VSAM KSDS), ACCTFILE (VSAM KSDS) | (none — returns data to caller) | (none — uses inline FDs) |
| 8 | CBTRN01C.cbl | 491 | Validate and post records from daily transaction file | DALYTRAN (sequential), CUSTFILE (VSAM KSDS), XREFFILE (VSAM KSDS), CARDFILE (VSAM KSDS), ACCTFILE (VSAM KSDS) | TRANFILE (VSAM KSDS, write) | CVTRA06Y, CVCUS01Y, CVACT03Y, CVACT02Y, CVACT01Y, CVTRA05Y |
| 9 | CBTRN02C.cbl | 731 | Post daily transactions — create category balances and update master | DALYTRAN (sequential), XREFFILE (VSAM KSDS) | TRANFILE (VSAM KSDS, write), DALYREJS (sequential, rejects), ACCTFILE (VSAM KSDS, rewrite), TCATBALF (VSAM KSDS, write/rewrite) | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y |
| 10 | CBTRN03C.cbl | 649 | Print the daily transaction detail report | TRANFILE (sequential), CARDXREF (VSAM KSDS), TRANTYPE (VSAM KSDS), TRANCATG (VSAM KSDS), DATEPARM (sequential) | TRANREPT (sequential report file) | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |

### 1.2 Online (CICS) Programs

| # | Filename | LOC | Purpose | CICS Files Accessed | CICS Operations | Copybooks Referenced |
|---|----------|-----|---------|--------------------|-----------------|--------------------|
| 1 | COSGN00C.cbl | 260 | Sign-on screen — authenticate users | USRSEC (READ) | RECEIVE, SEND, ASSIGN, READ, XCTL | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 2 | COADM01C.cbl | 268 | Admin menu — hub for admin functions | (none directly) | RETURN, SEND, RECEIVE, XCTL (via COMMAREA) | COCOM01Y, COADM02Y, COADM01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 3 | COMEN01C.cbl | 282 | Main menu — hub for regular user functions (10 XCTL targets) | (none directly) | RETURN, SEND, RECEIVE, XCTL (via COMMAREA) | COCOM01Y, COMEN02Y, COMEN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 4 | COACTVWC.cbl | 941 | View account details with card and customer info | ACCTFILE (READ), CARDFILE (READ), CARDXREF (READ) | HANDLE ABEND, XCTL, RETURN, SEND MAP, RECEIVE MAP, READ (×3) | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COACTVW, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CSSTRPFY |
| 5 | COACTUPC.cbl | 4,236 | Account update — exhaustive field validation (date, SSN, phone, state, ZIP) | ACCTFILE (READ/REWRITE), CARDXREF (READ), CUSTFILE (READ), CARDFILE (READ) | HANDLE ABEND, XCTL, RETURN, SEND MAP, RECEIVE MAP, READ (×5), REWRITE (×3) | CSUTLDWY, CVCRD01Y, CSLKPCDY, DFHBMSCA, DFHAID, COTTL01Y, COACTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT03Y, CVCUS01Y, COCOM01Y, CSSETATY (×39), CSSTRPFY, CSUTLDPY |
| 6 | COCRDLIC.cbl | 1,459 | List credit cards with paginated browse | CARDFILE (STARTBR, READNEXT, READPREV, ENDBR) | XCTL (×3), RETURN, SEND MAP, RECEIVE MAP, STARTBR, READNEXT, READPREV, ENDBR | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY |
| 7 | COCRDSLC.cbl | 887 | View credit card detail with customer info | CARDFILE (READ), CUSTFILE (READ) | HANDLE ABEND, XCTL, RETURN, SEND MAP, RECEIVE MAP, READ (×2) | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDSL, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 8 | COCRDUPC.cbl | 1,560 | Update credit card details | CARDFILE (READ/REWRITE), CUSTFILE (READ) | HANDLE ABEND, XCTL, RETURN, SEND MAP, RECEIVE MAP, READ (×2), REWRITE | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 9 | COTRN00C.cbl | 699 | List transactions with paginated browse | TRANSACT (STARTBR, READNEXT, READPREV, ENDBR) | RETURN, SEND (×2), RECEIVE, STARTBR, READNEXT, READPREV, ENDBR | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 10 | COTRN01C.cbl | 330 | View a single transaction | TRANSACT (READ) | RETURN, SEND, RECEIVE, READ | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 11 | COTRN02C.cbl | 783 | Add a new transaction | ACCTFILE (READ), CARDXREF (READ), TRANSACT (STARTBR/READPREV/ENDBR/WRITE) | RETURN, SEND, RECEIVE, READ (×2), STARTBR, READPREV, ENDBR, WRITE | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y, DFHAID, DFHBMSCA |
| 12 | COBIL00C.cbl | 572 | Bill payment — pay account balance | ACCTFILE (READ/REWRITE), CARDXREF (READ), TRANSACT (STARTBR/READPREV/ENDBR/WRITE) | RETURN, ASKTIME, FORMATTIME, SEND, RECEIVE, READ (×2), REWRITE, STARTBR, READPREV, ENDBR, WRITE | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 13 | CORPT00C.cbl | 649 | Submit batch report JCL via transient data queue | (none — submits JCL) | RETURN, WRITEQ TD, SEND (×2), RECEIVE | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 14 | COUSR00C.cbl | 695 | List all users with paginated browse | USRSEC (STARTBR, READNEXT, READPREV, ENDBR) | RETURN, XCTL (×3), SEND (×2), RECEIVE, STARTBR, READNEXT, READPREV, ENDBR | COCOM01Y, COUSR00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 15 | COUSR01C.cbl | 299 | Add a new user to USRSEC file | USRSEC (WRITE) | RETURN, SEND, RECEIVE, WRITE | COCOM01Y, COUSR01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 16 | COUSR02C.cbl | 414 | Update an existing user in USRSEC file | USRSEC (READ/REWRITE) | RETURN, SEND, RECEIVE, READ, REWRITE | COCOM01Y, COUSR02, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 17 | COUSR03C.cbl | 359 | Delete a user from USRSEC file | USRSEC (READ/DELETE) | RETURN, SEND, RECEIVE, READ, DELETE | COCOM01Y, COUSR03, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |

### 1.3 Utility Programs

| # | Filename | LOC | Purpose | External Calls | Copybooks |
|---|----------|-----|---------|---------------|-----------|
| 1 | CSUTLDTC.cbl | 157 | Date validation utility — validates CCYYMMDD dates using LE services | CALL "CEEDAYS" | (none — standalone) |

---

## 2. JCL Job Catalog

### 2.1 Data Setup Jobs (VSAM Cluster Definition)

| # | JCL File | Purpose | Steps | VSAM Dataset |
|---|----------|---------|-------|-------------|
| 1 | ACCTFILE.jcl | Define account data VSAM KSDS | STEP05: DELETE cluster → STEP10: DEFINE cluster (KEYS 11,0 RECSIZE 300) → STEP15: REPRO from PS | AWS.M2.CARDDEMO.ACCTDATA.VSAM.KSDS |
| 2 | CARDFILE.jcl | Define card data VSAM KSDS + AIX on ACCT-ID | CLCIFIL: Close CICS files → STEP05: DELETE → STEP10: DEFINE (KEYS 16,0 RECSIZE 150) → STEP15: REPRO → STEP40: DEFINE AIX → STEP50: DEFINE PATH → STEP60: BLDINDEX → OPCIFIL: Open CICS files | AWS.M2.CARDDEMO.CARDDATA.VSAM.KSDS |
| 3 | CUSTFILE.jcl | Define customer data VSAM KSDS | CLCIFIL: Close CICS → STEP05: DELETE → STEP10: DEFINE (KEYS 9,0 RECSIZE 500) → STEP15: REPRO → OPCIFIL: Open CICS | AWS.M2.CARDDEMO.CUSTDATA.VSAM.KSDS |
| 4 | XREFFILE.jcl | Define card cross-reference VSAM KSDS + AIX on ACCT-ID | STEP05: DELETE → STEP10: DEFINE (KEYS 16,0 RECSIZE 50) → STEP15: REPRO → STEP20: DEFINE AIX → STEP25: DEFINE PATH → STEP30: BLDINDEX | AWS.M2.CARDDEMO.CARDXREF.VSAM.KSDS |
| 5 | TRANFILE.jcl | Define transaction master VSAM KSDS + AIX on processed timestamp | CLCIFIL: Close CICS → STEP05: DELETE → STEP10: DEFINE (KEYS 16,0 RECSIZE 350) → STEP15: REPRO → STEP20: DEFINE AIX → STEP25: DEFINE PATH → STEP30: BLDINDEX → OPCIFIL: Open CICS | AWS.M2.CARDDEMO.TRANSACT.VSAM.KSDS |
| 6 | TCATBALF.jcl | Define transaction category balance VSAM KSDS | STEP05: DELETE → STEP10: DEFINE (KEYS 17,0 RECSIZE 50) → STEP15: REPRO | AWS.M2.CARDDEMO.TCATBALF.VSAM.KSDS |
| 7 | DISCGRP.jcl | Define disclosure group VSAM KSDS | STEP05: DELETE → STEP10: DEFINE (KEYS 16,0 RECSIZE 50) → STEP15: REPRO | AWS.M2.CARDDEMO.DISCGRP.VSAM.KSDS |
| 8 | TRANTYPE.jcl | Define transaction type VSAM KSDS | STEP05: DELETE → STEP10: DEFINE (KEYS 2,0 RECSIZE 60) → STEP15: REPRO | AWS.M2.CARDDEMO.TRANTYPE.VSAM.KSDS |
| 9 | TRANCATG.jcl | Define transaction category VSAM KSDS | STEP05: DELETE → STEP10: DEFINE (KEYS 6,0 RECSIZE 60) → STEP15: REPRO | AWS.M2.CARDDEMO.TRANCATG.VSAM.KSDS |
| 10 | DUSRSECJ.jcl | Define user security VSAM KSDS with inline seed data | PREDEL: Delete PS → STEP01: IEBGENER (create PS from inline) → STEP02: DEFINE VSAM (KEYS 8,0 RECSIZE 80) → STEP03: REPRO to VSAM | AWS.M2.CARDDEMO.USRSEC.VSAM.KSDS |
| 11 | DEFCUST.jcl | Alternate customer file definition (legacy naming) | STEP05: DELETE → STEP05: DEFINE (KEYS 10,0 RECSIZE 500) | AWS.CUSTDATA.CLUSTER |

### 2.2 Batch Processing Jobs

| # | JCL File | Purpose | Program Executed | Input Datasets | Output Datasets |
|---|----------|---------|-----------------|---------------|----------------|
| 1 | POSTTRAN.jcl | Post daily transactions | CBTRN02C | DALYTRAN (PS), XREFFILE (VSAM), ACCTFILE (VSAM), TCATBALF (VSAM) | TRANFILE (VSAM), DALYREJS (GDG +1) |
| 2 | INTCALC.jcl | Interest calculation | CBACT04C (PARM='2022071800') | TCATBALF (VSAM), XREFFILE (VSAM), ACCTFILE (VSAM), DISCGRP (VSAM) | TRANSACT (sequential GDG +1, system-generated interest transactions) |
| 3 | TRANREPT.jcl | Transaction detail report | STEP05R: REPROC (backup TRANSACT) → STEP05R: SORT (filter by date, sort by card#) → STEP10R: CBTRN03C | TRANSACT (VSAM), CARDXREF (VSAM), TRANTYPE (VSAM), TRANCATG (VSAM), DATEPARM | TRANREPT (GDG +1, LRECL=133) |
| 4 | CREASTMT.JCL | Generate account statements | CBSTM03A (calls CBSTM03B) | TRNXFILE (VSAM), XREFFILE (VSAM), CUSTFILE (VSAM), ACCTFILE (VSAM) | STMTFILE (text), HTMLFILE (HTML) |
| 5 | READACCT.jcl | Print account master | CBACT01C | ACCTFILE (VSAM) | SYSOUT |
| 6 | READCARD.jcl | Print card master | CBACT02C | CARDFILE (VSAM) | SYSOUT |
| 7 | READCUST.jcl | Print customer master | CBCUS01C | CUSTFILE (VSAM) | SYSOUT |
| 8 | READXREF.jcl | Print cross-reference file | CBACT03C | XREFFILE (VSAM) | SYSOUT |
| 9 | PRTCATBL.jcl | Print transaction category balance file | REPROC → SORT (format + sort by acct/type/cat) | TCATBALF (VSAM) | TCATBALF.REPT, TCATBALF.BKUP (GDG +1) |

### 2.3 Infrastructure / Utility Jobs

| # | JCL File | Purpose | Steps |
|---|----------|---------|-------|
| 1 | CBADMCDJ.jcl | Create CICS resource definitions (CSD) for CardDemo | STEP1: DFHCSDUP — defines programs, mapsets, transactions, TDQs, files for CICS region |
| 2 | CLOSEFIL.jcl | Close all VSAM files in CICS region | CLCIFIL: SDSF commands to close TRANSACT, CCXREF, ACCTDAT, CXACAIX, USRSEC |
| 3 | OPENFIL.jcl | Open all VSAM files in CICS region | OPCIFIL: SDSF commands to open TRANSACT, CCXREF, ACCTDAT, CXACAIX, USRSEC |
| 4 | DEFGDGB.jcl | Define GDG bases needed by CardDemo | STEP05: Define 6 GDG bases: TRANSACT.BKUP, TRANSACT.DALY, TRANREPT, TCATBALF.BKUP, SYSTRAN, TRANSACT.COMBINED |
| 5 | DALYREJS.jcl | Define GDG base for daily rejects | STEP05: Define GDG AWS.M2.CARDDEMO.DALYREJS (LIMIT 5) |
| 6 | REPTFILE.jcl | Define GDG base for report file | STEP05: Define GDG AWS.M2.CARDDEMO.TRANREPT (LIMIT 10) |
| 7 | COMBTRAN.jcl | Combine backup + system-generated transactions | STEP05R: SORT (merge TRANSACT.BKUP + SYSTRAN, sort by TRAN-ID) → STEP10: REPRO to TRANSACT VSAM | AWS.M2.CARDDEMO.TRANSACT.COMBINED (GDG) |
| 8 | TRANBKP.jcl | Backup and recreate transaction master | STEP05R: REPROC (backup) → STEP05: DELETE cluster → STEP10: DEFINE cluster (KEYS 16,0 RECSIZE 350) | AWS.M2.CARDDEMO.TRANSACT.BKUP (GDG) |
| 9 | TRANIDX.jcl | Create alternate index on transaction master | STEP20: DEFINE AIX → STEP25: DEFINE PATH → STEP30: BLDINDEX | AIX on processed timestamp (KEYS 26,304) |

---

## 3. Daily Batch Pipeline Sequence

```
1. POSTTRAN.jcl  (CBTRN02C)  — Ingest DALYTRAN → validate → write TRANSACT + TCATBALF, update ACCTFILE
        ↓
2. INTCALC.jcl   (CBACT04C)  — Read TCATBALF → lookup DISCGRP rates → compute interest → rewrite ACCTFILE
        ↓
3. COMBTRAN.jcl   (SORT)      — Merge TRANSACT.BKUP + SYSTRAN → reload TRANSACT VSAM
        ↓
4. CREASTMT.JCL  (CBSTM03A)  — Read TRNX + XREF + CUST + ACCT → generate text + HTML statements
        ↓
5. TRANREPT.jcl  (CBTRN03C)  — Sort + filter transactions → produce formatted daily report
        ↓
6. TRANBKP.jcl   (IDCAMS)    — Backup TRANSACT VSAM → recreate empty cluster for next cycle
```
