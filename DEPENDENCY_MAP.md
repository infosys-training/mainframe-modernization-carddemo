# DEPENDENCY MAP — CardDemo COBOL Estate

## 1. Inter-Program Call Graph

### 1.1 Online (CICS) Navigation — XCTL Chain

```
                       ┌──────────────┐
                       │  COSGN00C    │  (Entry point — TRANID CC00)
                       │  Sign-on     │
                       └──────┬───────┘
                              │ XCTL (based on SEC-USR-TYPE)
               ┌──────────────┴──────────────┐
               ▼                              ▼
      ┌────────────────┐             ┌────────────────┐
      │  COADM01C      │             │  COMEN01C      │
      │  Admin Menu    │             │  Main Menu     │
      │  (Type = 'A')  │             │  (Type = 'U')  │
      └───────┬────────┘             └───────┬────────┘
              │ XCTL                         │ XCTL (10 targets)
     ┌────┬───┴───┬────┐         ┌──────┬───┴───┬──────┬──────┐
     ▼    ▼       ▼    ▼         ▼      ▼       ▼      ▼      ▼
 COUSR00C COUSR01C COUSR02C COUSR03C  COACTVWC COACTUPC COCRDLIC ...
 User     User     User     User      Acct     Acct     Card
 List     Add      Update   Delete    View     Update   List
```

**Full COMEN01C XCTL targets** (from COMEN02Y.cpy):

| # | Target | Function |
|---|--------|----------|
| 1 | COACTVWC | Account View |
| 2 | COACTUPC | Account Update |
| 3 | COCRDLIC | Credit Card List |
| 4 | COCRDSLC | Credit Card Detail View |
| 5 | COCRDUPC | Credit Card Update |
| 6 | COTRN00C | Transaction List |
| 7 | COTRN01C | Transaction View |
| 8 | COTRN02C | Transaction Add |
| 9 | CORPT00C | Transaction Reports |
| 10 | COBIL00C | Bill Payment |

**Within-program XCTL returns:** Each leaf program can XCTL back to COMEN01C (or COADM01C for admin programs) via CDEMO-FROM-PROGRAM in the COMMAREA.

### 1.2 Batch CALL Graph

```
CBSTM03A ──CALL──▶ CBSTM03B
  (Statement         (File I/O
   Generator)          Subroutine)

CBACT01C ──CALL──▶ CEE3ABD        (LE abnormal termination handler)
CBACT02C ──CALL──▶ CEE3ABD
CBACT03C ──CALL──▶ CEE3ABD
CBTRN01C ──CALL──▶ CEE3ABD
CBTRN02C ──CALL──▶ CEE3ABD

CORPT00C ──CALL──▶ CSUTLDTC      (Date validation utility)
COTRN02C ──CALL──▶ CSUTLDTC      (Date validation utility, ×2)
CSUTLDTC ──CALL──▶ CEEDAYS       (LE date conversion service)

COACTUPC ──COPY CSUTLDPY──▶ (inline CALL 'CSUTLDTC')
  (Account Update contains date validation paragraphs inlined via COPY)
```

### 1.3 External Dependencies

| External Program | Type | Called By | Purpose |
|-----------------|------|-----------|---------|
| CEE3ABD | LE Runtime | CBACT01C, CBACT02C, CBACT03C, CBTRN01C, CBTRN02C | Abnormal termination — invoked on fatal file status errors |
| CEEDAYS | LE Runtime | CSUTLDTC | Convert date to Lilian format for validation |
| SDSF | MVS Utility | CLOSEFIL.jcl, OPENFIL.jcl, CARDFILE.jcl, CUSTFILE.jcl, TRANFILE.jcl | CICS file open/close via operator commands |
| IDCAMS | MVS Utility | All data-setup JCL | VSAM cluster define/delete/repro |
| SORT | MVS Utility | COMBTRAN.jcl, TRANREPT.jcl, PRTCATBL.jcl | Sort/merge/filter datasets |
| IEFBR14 | MVS Utility | TRANBKP.jcl, PRTCATBL.jcl, DUSRSECJ.jcl | Null program for DD allocation/deletion |
| IEBGENER | MVS Utility | DUSRSECJ.jcl | Copy inline data to sequential dataset |
| DFHCSDUP | CICS Utility | CBADMCDJ.jcl | CICS CSD batch update |

---

## 2. Copybook Dependency Matrix

### 2.1 Programs → Copybooks

| Copybook | Batch Programs Using It | Online Programs Using It | Total Refs |
|----------|------------------------|--------------------------|------------|
| COCOM01Y | — | All 18 CICS programs | 18 |
| DFHAID | — | All 18 CICS programs | 18 |
| DFHBMSCA | — | All 18 CICS programs | 18 |
| COTTL01Y | — | All 18 CICS programs | 18 |
| CSDAT01Y | — | All 18 CICS programs | 18 |
| CSMSG01Y | — | All 18 CICS programs | 18 |
| CSUSR01Y | — | COSGN00C, COADM01C, COMEN01C, COUSR00-03C | 7 |
| CVACT01Y | CBACT01C, CBACT04C, CBSTM03A, CBTRN01C, CBTRN02C | COACTUPC, COACTVWC, COBIL00C, COTRN02C | 9 |
| CVACT03Y | CBACT03C, CBACT04C, CBSTM03A, CBTRN01C, CBTRN02C, CBTRN03C | COACTUPC, COACTVWC, COBIL00C, COTRN02C | 10 |
| CVACT02Y | CBACT02C, CBTRN01C | COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC | 6 |
| CVTRA05Y | CBACT04C, CBTRN01C, CBTRN02C, CBTRN03C | COTRN00C, COTRN01C, COTRN02C, COBIL00C, CORPT00C | 9 |
| CVCUS01Y | CBCUS01C, CBTRN01C | COACTUPC, COACTVWC, COCRDSLC, COCRDUPC | 6 |
| CVCRD01Y | — | COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC | 5 |
| CVTRA06Y | CBTRN01C, CBTRN02C | — | 2 |
| CVTRA01Y | CBACT04C, CBTRN02C | — | 2 |
| CVTRA02Y | CBACT04C | — | 1 |
| CVTRA03Y | CBTRN03C | — | 1 |
| CVTRA04Y | CBTRN03C | — | 1 |
| CVTRA07Y | CBTRN03C | — | 1 |
| COSTM01 | CBSTM03A | — | 1 |
| CUSTREC | CBSTM03A | — | 1 |
| CSLKPCDY | — | COACTUPC | 1 |
| CSUTLDWY | — | COACTUPC | 1 |
| CSUTLDPY | — | COACTUPC | 1 |
| CSSETATY | — | COACTUPC (×39) | 1 |
| CSSTRPFY | — | COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC | 5 |
| CSMSG02Y | — | COACTUPC, COACTVWC, COCRDSLC, COCRDUPC | 4 |
| COADM02Y | — | COADM01C | 1 |
| COMEN02Y | — | COMEN01C | 1 |
| UNUSED1Y | — | — | 0 |

---

## 3. Dataset Lineage — VSAM File Access Matrix

### 3.1 Programs → VSAM Files

| VSAM Dataset | Key | JCL Setup | Programs That READ | Programs That WRITE/REWRITE |
|-------------|-----|-----------|-------------------|---------------------------|
| ACCTDATA.VSAM.KSDS | ACCT-ID (11,0) | ACCTFILE.jcl | CBACT01C, CBACT04C, CBSTM03B, CBTRN01C, COACTUPC, COACTVWC, COBIL00C, COTRN02C | CBACT04C (rewrite), CBTRN02C (rewrite), COACTUPC (rewrite), COBIL00C (rewrite) |
| CARDDATA.VSAM.KSDS | CARD-NUM (16,0) | CARDFILE.jcl | CBACT02C, CBTRN01C, COACTUPC, COACTVWC, COCRDLIC (browse), COCRDSLC, COCRDUPC | COCRDUPC (rewrite) |
| CUSTDATA.VSAM.KSDS | CUST-ID (9,0) | CUSTFILE.jcl | CBCUS01C, CBSTM03B, CBTRN01C, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC | (read-only by all programs) |
| CARDXREF.VSAM.KSDS | XREF-CARD-NUM (16,0) | XREFFILE.jcl | CBACT03C, CBACT04C, CBSTM03B, CBTRN01C, CBTRN02C, CBTRN03C, COACTUPC, COACTVWC, COBIL00C, COTRN02C | (read-only by all programs) |
| TRANSACT.VSAM.KSDS | TRAN-ID (16,0) | TRANFILE.jcl | COTRN00C (browse), COTRN01C, COBIL00C (browse) | CBTRN01C (write), CBTRN02C (write), COTRN02C (write), COBIL00C (write) |
| TCATBALF.VSAM.KSDS | TRAN-CAT-KEY (17,0) | TCATBALF.jcl | CBACT04C | CBTRN02C (write/rewrite) |
| DISCGRP.VSAM.KSDS | DIS-GROUP-KEY (16,0) | DISCGRP.jcl | CBACT04C | (read-only) |
| TRANTYPE.VSAM.KSDS | TRAN-TYPE (2,0) | TRANTYPE.jcl | CBTRN03C | (read-only) |
| TRANCATG.VSAM.KSDS | TRAN-CAT-KEY (6,0) | TRANCATG.jcl | CBTRN03C | (read-only) |
| USRSEC.VSAM.KSDS | SEC-USR-ID (8,0) | DUSRSECJ.jcl | COSGN00C, COUSR00C (browse), COUSR02C, COUSR03C | COUSR01C (write), COUSR02C (rewrite), COUSR03C (delete) |

### 3.2 Non-VSAM Datasets (Sequential / GDG)

| Dataset | Format | Read By | Written By | JCL Jobs |
|---------|--------|---------|-----------|----------|
| DALYTRAN.PS | Sequential (LRECL=350) | CBTRN02C (via POSTTRAN.jcl) | (external feed) | POSTTRAN.jcl |
| DALYREJS (GDG) | Sequential (LRECL=430) | (not processed) | CBTRN02C (rejects) | POSTTRAN.jcl |
| SYSTRAN (GDG) | Sequential (LRECL=350) | COMBTRAN.jcl (SORT input) | CBACT04C (interest transactions) | INTCALC.jcl, COMBTRAN.jcl |
| TRANSACT.BKUP (GDG) | Sequential (LRECL=350) | COMBTRAN.jcl, TRANREPT.jcl (SORT) | TRANBKP.jcl, TRANREPT.jcl (REPROC) | TRANBKP.jcl, TRANREPT.jcl, COMBTRAN.jcl |
| TRANSACT.COMBINED (GDG) | Sequential (LRECL=350) | COMBTRAN.jcl STEP10 (REPRO to VSAM) | COMBTRAN.jcl STEP05R (SORT output) | COMBTRAN.jcl |
| TRANSACT.DALY (GDG) | Sequential (LRECL=350) | CBTRN03C (via TRANREPT.jcl) | TRANREPT.jcl STEP05R (SORT output) | TRANREPT.jcl |
| TRANREPT (GDG) | Sequential (LRECL=133) | (printed/viewed) | CBTRN03C | TRANREPT.jcl |
| TCATBALF.BKUP (GDG) | Sequential (LRECL=50) | PRTCATBL.jcl STEP10R (SORT) | PRTCATBL.jcl STEP05R (REPROC) | PRTCATBL.jcl |
| TCATBALF.REPT | Sequential (LRECL=40) | (printed/viewed) | PRTCATBL.jcl STEP10R (SORT output) | PRTCATBL.jcl |
| DATEPARM | Sequential | CBTRN03C | (external config) | TRANREPT.jcl |
| STMTFILE | Sequential | (printed/viewed) | CBSTM03A | CREASTMT.JCL |
| HTMLFILE | Sequential | (browser/archive) | CBSTM03A | CREASTMT.JCL |

---

## 4. End-to-End Batch Pipeline Flow

### 4.1 Daily Processing Pipeline

```
External Feed                  ┌────────────────────────────┐
(DALYTRAN.PS) ────────────────▶│ 1. POSTTRAN.jcl            │
                               │    PGM=CBTRN02C            │
                               │    Read: DALYTRAN, XREFFILE │
                               │    Write: TRANFILE, ACCTFILE │
                               │           TCATBALF, DALYREJS │
                               └──────────────┬─────────────┘
                                              │
                                              ▼
                               ┌────────────────────────────┐
                               │ 2. INTCALC.jcl             │
                               │    PGM=CBACT04C            │
                               │    Read: TCATBALF, XREFFILE │
                               │          ACCTFILE, DISCGRP  │
                               │    Write: SYSTRAN (interest │
                               │           transactions)     │
                               │    Rewrite: ACCTFILE (bal)  │
                               └──────────────┬─────────────┘
                                              │
                                              ▼
                               ┌────────────────────────────┐
                               │ 3. COMBTRAN.jcl            │
                               │    PGM=SORT                │
                               │    Merge TRANSACT.BKUP     │
                               │     + SYSTRAN              │
                               │    → TRANSACT.COMBINED     │
                               │    → REPRO to TRANSACT VSAM│
                               └──────────────┬─────────────┘
                                              │
                               ┌──────────────┴─────────────┐
                               │                            │
                               ▼                            ▼
                ┌─────────────────────┐    ┌─────────────────────┐
                │ 4. CREASTMT.JCL     │    │ 5. TRANREPT.jcl     │
                │    PGM=CBSTM03A     │    │    STEP05R: SORT     │
                │    Read: TRNXFILE,  │    │    (filter by date)  │
                │     XREFFILE,       │    │    STEP10R: CBTRN03C │
                │     CUSTFILE,       │    │    Read: TRANSACT,   │
                │     ACCTFILE        │    │     CARDXREF,        │
                │    Write: STMTFILE, │    │     TRANTYPE,        │
                │     HTMLFILE        │    │     TRANCATG,        │
                │                     │    │     DATEPARM         │
                │    (text + HTML     │    │    Write: TRANREPT   │
                │     statements)     │    │    (formatted report)│
                └─────────────────────┘    └─────────────────────┘
                                              │
                                              ▼
                               ┌────────────────────────────┐
                               │ 6. TRANBKP.jcl            │
                               │    Backup TRANSACT VSAM    │
                               │    → TRANSACT.BKUP (GDG)   │
                               │    Recreate empty cluster  │
                               └────────────────────────────┘
```

### 4.2 One-Time Setup Pipeline (Run order)

```
1. DEFGDGB.jcl    — Define all GDG bases (TRANSACT.BKUP, TRANSACT.DALY,
                     TRANREPT, TCATBALF.BKUP, SYSTRAN, TRANSACT.COMBINED)
2. DALYREJS.jcl   — Define DALYREJS GDG base
3. REPTFILE.jcl   — Define TRANREPT GDG base (alternative limit)
4. ACCTFILE.jcl   — Create ACCTDATA VSAM + seed data
5. CARDFILE.jcl   — Create CARDDATA VSAM + AIX + seed data
6. CUSTFILE.jcl   — Create CUSTDATA VSAM + seed data
7. XREFFILE.jcl   — Create CARDXREF VSAM + AIX + seed data
8. TRANFILE.jcl   — Create TRANSACT VSAM + AIX + seed data
9. TCATBALF.jcl   — Create TCATBALF VSAM + seed data
10. DISCGRP.jcl   — Create DISCGRP VSAM + seed data
11. TRANTYPE.jcl  — Create TRANTYPE VSAM + seed data
12. TRANCATG.jcl  — Create TRANCATG VSAM + seed data
13. DUSRSECJ.jcl  — Create USRSEC VSAM + seed user data
14. CBADMCDJ.jcl  — Define CICS resources (CSD): programs, transactions, maps, files
15. OPENFIL.jcl   — Open all VSAM files in CICS region
```

---

## 5. CICS Resource Definitions (from CBADMCDJ.jcl)

The CSD batch defines the runtime mapping between CICS transaction IDs and programs:

| Transaction ID | Program | Type |
|---------------|---------|------|
| CC00 | COSGN00C | Sign-on |
| CA00 | COADM01C | Admin menu |
| CM00 | COMEN01C | Main menu |
| CA01 | COACTVWC | Account view |
| CA02 | COACTUPC | Account update |
| CC01 | COCRDLIC | Card list |
| CC02 | COCRDSLC | Card detail |
| CC03 | COCRDUPC | Card update |
| CT00 | COTRN00C | Transaction list |
| CT01 | COTRN01C | Transaction view |
| CT02 | COTRN02C | Transaction add |
| CB00 | COBIL00C | Bill payment |
| CR00 | CORPT00C | Reports |
| CU00 | COUSR00C | User list |
| CU01 | COUSR01C | User add |
| CU02 | COUSR02C | User update |
| CU03 | COUSR03C | User delete |
