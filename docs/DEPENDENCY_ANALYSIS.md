# CardDemo — Dependency Analysis Report

**Repo:** `infosys-training/mainframe-modernization-carddemo`  
**Date:** 2026-06-09

---

## 1. Estate Summary

| Metric | Value |
|--------|-------|
| COBOL Programs | 27 (in `app/cbl/`) |
| Copybooks | 27 (in `app/cpy/`) + 17 BMS copybooks (`app/cpy-bms/`) |
| JCL Jobs | 30 (in `app/jcl/`) |
| BMS Maps | 17 (in `app/bms/`) |
| Total COBOL LOC | 19,254 |
| CICS Online Programs | 17 |
| Batch Programs | 10 |
| VSAM Files (CSD-defined) | 8 |
| CICS Transactions | 18 |
| Data Files (EBCDIC/ASCII) | 12 / 9 |

**No Java backend, `pom.xml`, or `build.gradle` exists** — this repo is pure mainframe COBOL/CICS/VSAM.

---

## 2. Program-to-Program Dependencies (XCTL / CALL Graph)

### 2.1 Online Navigation (CICS XCTL)

```
COSGN00C (Sign-On — Entry Point, txn CC00)
├── [Admin path] ──► COADM01C (Admin Menu, txn CA00)
│   ├──► COUSR00C (User List, txn CU00) ──► COUSR02C (Update, CU02)
│   │                                   ──► COUSR03C (Delete, CU03)
│   └──► COUSR01C (User Add, txn CU01)
│
└── [User path] ──► COMEN01C (Main Menu — Hub, txn CM00)
    ├──► COACTVWC (Account View, txn CAVW)
    ├──► COACTUPC (Account Update, txn CAUP)
    ├──► COCRDLIC (Card List, txn CCLI) ──► COCRDSLC (View, CCDL)
    │                                   ──► COCRDUPC (Update, CCUP)
    ├──► COTRN00C (Txn List, txn CT00)  ──► COTRN01C (View, CT01)
    ├──► COTRN02C (Txn Add, txn CT02)
    ├──► COBIL00C (Bill Pay, txn CB00)
    └──► CORPT00C (Reports, txn CR00)
```

All online programs route back to `COSGN00C` on session timeout/PF3.

### 2.2 Batch CALL Dependencies

| Caller | Callee | Purpose |
|--------|--------|---------|
| CBSTM03A | CBSTM03B | Statement I/O submodule (13 calls) |
| CORPT00C | CSUTLDTC | Date utility (2 calls) |
| COTRN02C | CSUTLDTC | Date utility (2 calls) |
| CSUTLDTC | CEEDAYS | LE intrinsic date conversion |
| CBACT01C | CEE3ABD | LE abend handler |
| CBACT02C | CEE3ABD | LE abend handler |
| CBACT03C | CEE3ABD | LE abend handler |
| CBACT04C | CEE3ABD | LE abend handler |
| CBCUS01C | CEE3ABD | LE abend handler |
| CBTRN01C | CEE3ABD | LE abend handler |
| CBTRN02C | CEE3ABD | LE abend handler |
| CBTRN03C | CEE3ABD | LE abend handler |

**External (Assembler/LE) dependencies:** `CEE3ABD`, `CEEDAYS` (Language Environment runtime routines — not in this repo).

---

## 3. Copybook Dependencies (Program → Copybook Matrix)

### 3.1 Most-Referenced Copybooks

| Copybook | Programs Using It | Purpose |
|----------|-------------------|---------|
| COCOM01Y | 16 | CICS COMMAREA (inter-program data contract) |
| COTTL01Y | 16 | Screen title line |
| CSDAT01Y | 16 | Date working storage |
| CSMSG01Y | 16 | Message area |
| DFHBMSCA | 16 | BMS attribute constants |
| DFHAID | 16 | AID key definitions (PF keys) |
| CSUSR01Y | 11 | User/security record |
| CVACT01Y | 8 | Account record layout (300 bytes) |
| CVACT03Y | 8 | Card-Account XREF (50 bytes) |
| CVTRA05Y | 9 | Transaction record (350 bytes) |
| CVACT02Y | 7 | Card record (150 bytes) |
| CVCUS01Y | 6 | Customer record (500 bytes) |
| CSSETATY | 1 (COACTUPC) | Screen attribute macro — used 39× via COPY REPLACING |
| CSLKPCDY | 1 (COACTUPC) | State/ZIP/phone validation lookup (1,318 LOC!) |

### 3.2 Per-Program Copybook Count

| Program | # Copybooks | Notable Includes |
|---------|-------------|-----------------|
| COACTUPC | 15 + 39 REPLACING | CSLKPCDY, CSUTLDWY, CSUTLDPY, CSSTRPFY |
| COACTVWC | 14 | CSSTRPFY, CVCRD01Y |
| COCRDLIC | 11 | CSSTRPFY, CVCRD01Y |
| COCRDSLC | 12 | CSSTRPFY, CVCRD01Y |
| COCRDUPC | 12 | CSSTRPFY, CVCRD01Y |
| CBTRN01C | 6 | CVTRA06Y, CVCUS01Y, CVACT02Y |
| CBACT04C | 5 | CVTRA01Y, CVTRA02Y, CVTRA05Y |
| CBSTM03A | 4 | COSTM01, CUSTREC |

### 3.3 BMS Map Copybooks (app/cpy-bms/)

Each online program pairs with a BMS map copybook:
`COACTUP.CPY`, `COACTVW.CPY`, `COADM01.CPY`, `COBIL00.CPY`, `COCRDLI.CPY`, `COCRDSL.CPY`, `COCRDUP.CPY`, `COMEN01.CPY`, `CORPT00.CPY`, `COSGN00.CPY`, `COTRN00.CPY`, `COTRN01.CPY`, `COTRN02.CPY`, `COUSR00.CPY`, `COUSR01.CPY`, `COUSR02.CPY`, `COUSR03.CPY`

---

## 4. VSAM File Dependencies

### 4.1 CSD File Definitions (CICS Resource Table)

| Logical Name | VSAM Dataset | Record Type |
|--------------|--------------|-------------|
| ACCTDAT | AWS.M2.CARDDEMO.ACCTDATA.VSAM.KSDS | Account |
| CARDDAT | AWS.M2.CARDDEMO.CARDDATA.VSAM.KSDS | Card |
| CARDAIX | AWS.M2.CARDDEMO.CARDDATA.VSAM.AIX.PATH | Card (Alt Index) |
| CCXREF | AWS.M2.CARDDEMO.CARDXREF.VSAM.KSDS | Card-Acct XREF |
| CXACAIX | (Alt Index path for XREF) | XREF (Alt Index) |
| CUSTDAT | AWS.M2.CARDDEMO.CUSTDATA.VSAM.KSDS | Customer |
| TRANSACT | AWS.M2.CARDDEMO.TRANSACT.VSAM.KSDS | Transaction |
| USRSEC | AWS.M2.CARDDEMO.USRSEC.VSAM.KSDS | User/Security |

### 4.2 Program → VSAM File Access Matrix (Online)

| Program | ACCTDAT | CARDDAT | CARDAIX | CCXREF | CXACAIX | CUSTDAT | TRANSACT | USRSEC |
|---------|:-------:|:-------:|:-------:|:------:|:-------:|:-------:|:--------:|:------:|
| COACTUPC | R/W | R | R | — | R | R | — | — |
| COACTVWC | R | R | R | — | R | R | — | — |
| COBIL00C | R/W | — | — | — | R | — | R/W | — |
| COCRDLIC | — | R | R | — | — | — | — | — |
| COCRDSLC | — | R | R | — | — | — | — | — |
| COCRDUPC | — | R/W | R | — | — | — | — | — |
| COTRN00C | — | — | — | — | — | — | R | — |
| COTRN01C | — | — | — | — | — | — | R | — |
| COTRN02C | R | — | — | R | R | — | R/W | — |
| CORPT00C | — | — | — | — | — | — | R | — |
| COSGN00C | — | — | — | — | — | — | — | R |
| COADM01C | — | — | — | — | — | — | — | R |
| COMEN01C | — | — | — | — | — | — | — | R |
| COUSR00C | — | — | — | — | — | — | — | R |
| COUSR01C | — | — | — | — | — | — | — | W |
| COUSR02C | — | — | — | — | — | — | — | R/W |
| COUSR03C | — | — | — | — | — | — | — | R |

### 4.3 Batch File Usage (via JCL DD statements)

| JCL Job | Program | Files Accessed |
|---------|---------|----------------|
| POSTTRAN | CBTRN02C | DALYTRAN, TRANSACT, CARDXREF, ACCTDATA, TCATBALF |
| INTCALC | CBACT04C | ACCTDATA, CARDXREF, DISCGRP, TCATBALF |
| CREASTMT | (CBSTM03A) | ACCTDATA, CARDXREF, CUSTDATA, TRANSACT |
| TRANREPT | SORT→CBTRN03C | TRANSACT, CARDXREF, TRANCATG, TRANTYPE |
| READACCT | CBACT01C | ACCTDATA |
| READCARD | CBACT02C | CARDDATA |
| READCUST | CBCUS01C | CUSTDATA |
| READXREF | CBACT03C | CARDXREF |

---

## 5. Batch Pipeline Execution Order (Daily Cycle)

```
POSTTRAN (CBTRN02C) — Post daily transactions
       │ reads: DALYTRAN; writes: TRANSACT, ACCTDATA, TCATBALF
       ▼
INTCALC (CBACT04C) — Calculate interest
       │ reads: TCATBALF, CARDXREF, DISCGRP, ACCTDATA; writes: ACCTDATA
       ▼
CREASTMT (CBSTM03A→CBSTM03B) — Generate statements
       │ reads: CARDXREF, CUSTDATA, ACCTDATA, TRANSACT; writes: STMT-FILE, HTML-FILE
       ▼
TRANREPT (CBTRN03C) — Generate daily report
         reads: TRANSACT, CARDXREF, TRANTYPE, TRANCATG; writes: REPTFILE
```

**Data Flow Dependency:** POSTTRAN must complete before INTCALC (both write ACCTDATA). CREASTMT and TRANREPT depend on TRANSACT being posted.

---

## 6. CICS Transaction → Program Mapping

| Transaction | Program | Function |
|-------------|---------|----------|
| CC00 | COSGN00C | Sign-on (entry point) |
| CA00 | COADM01C | Admin menu |
| CM00 | COMEN01C | Main menu |
| CAUP | COACTUPC | Account update |
| CAVW | COACTVWC | Account view |
| CB00 | COBIL00C | Bill payment |
| CCLI | COCRDLIC | Card list |
| CCDL | COCRDSLC | Card detail |
| CCUP | COCRDUPC | Card update |
| CT00 | COTRN00C | Transaction list |
| CT01 | COTRN01C | Transaction view |
| CT02 | COTRN02C | Transaction add |
| CR00 | CORPT00C | Report request |
| CU00 | COUSR00C | User list |
| CU01 | COUSR01C | User add |
| CU02 | COUSR02C | User update |
| CU03 | COUSR03C | User delete |
| CDV1 | COCRDSEC | Card security |

---

## 7. External / Runtime Dependencies

| Dependency | Type | Required By | Notes |
|------------|------|-------------|-------|
| CICS TS | Runtime | All 17 online programs | EXEC CICS APIs (SEND, RECEIVE, READ, XCTL, etc.) |
| DFHAID | IBM Copybook | 16 programs | AID key byte definitions |
| DFHBMSCA | IBM Copybook | 16 programs | BMS attribute character constants |
| CEE3ABD | LE Runtime | 8 batch programs | Language Environment abnormal termination |
| CEEDAYS | LE Runtime | CSUTLDTC | LE date conversion intrinsic |
| IDCAMS | z/OS Utility | 10 JCL jobs | VSAM cluster definition/management |
| SORT | z/OS Utility | 2 JCL jobs | Data sorting (COMBTRAN, TRANREPT) |
| SDSF | z/OS Utility | 4 JCL jobs | File open/close operations |
| DFHCSDUP | CICS Utility | CBADMCDJ.jcl | CSD batch utility |
| IEFBR14 | z/OS Utility | 2 JCL jobs | Dummy step (allocation/deletion) |

---

## 8. Data File Inventory

### EBCDIC Data (Production Format)
| Dataset | Purpose |
|---------|---------|
| AWS.M2.CARDDEMO.ACCTDATA.PS | Account master |
| AWS.M2.CARDDEMO.CARDDATA.PS | Card master |
| AWS.M2.CARDDEMO.CUSTDATA.PS | Customer master |
| AWS.M2.CARDDEMO.CARDXREF.PS | Card-Account cross-reference |
| AWS.M2.CARDDEMO.DALYTRAN.PS | Daily transaction input |
| AWS.M2.CARDDEMO.DISCGRP.PS | Discount group reference |
| AWS.M2.CARDDEMO.TCATBALF.PS | Transaction category balance |
| AWS.M2.CARDDEMO.TRANCATG.PS | Transaction category codes |
| AWS.M2.CARDDEMO.TRANTYPE.PS | Transaction type codes |
| AWS.M2.CARDDEMO.USRSEC.PS | User security |

### ASCII Data (Modernization-Ready)
`acctdata.txt`, `carddata.txt`, `custdata.txt`, `cardxref.txt`, `dailytran.txt`, `discgrp.txt`, `tcatbal.txt`, `trancatg.txt`, `trantype.txt`

---

## 9. Complexity Hotspots

| Program | LOC | Copybooks | VSAM Files | Risk |
|---------|-----|-----------|------------|------|
| COACTUPC | 4,236 | 15+39 REPLACING | 5 | **HIGH** — Largest; heavy validation, COPY REPLACING macro |
| CBSTM03A/B | 924+230 | 4 | 4 | MEDIUM — Self-contained batch, 13 CALL statements |
| COCRDLIC | 1,459 | 11 | 2 | MEDIUM — Pagination pattern (STARTBR/READNEXT/READPREV) |
| COCRDUPC | 1,560 | 12 | 2 | MEDIUM — Card update with multi-file access |
| CBTRN02C | 731 | 6 | 5 | MEDIUM — Core posting; touches most files |

---

## 10. Key Observations & Risks

1. **Tight COMMAREA Coupling:** All 16 online programs share `COCOM01Y.cpy` (47-byte COMMAREA). Any field change impacts all programs.

2. **Hub-and-Spoke Architecture:** `COMEN01C` is the central routing hub for all user functions. `COADM01C` is the admin hub. Migration must preserve these navigation semantics.

3. **CSLKPCDY Validation Monolith:** At 1,318 LOC, this single copybook contains all US state, ZIP, and phone validation — used only by `COACTUPC` but critical for data integrity.

4. **No External DB/MQ/IMS in This Repo:** Unlike the knowledge notes referencing DB2/IMS/MQ programs, **this specific repo contains only VSAM-based programs**. The sub-application programs (IMS, DB2, MQ) referenced in knowledge notes exist in a different repo (`uc-legacy-modernization-cobol-to-java`).

5. **CSSETATY COPY REPLACING (39× in COACTUPC):** This macro pattern generates screen attribute-setting code for each field — a significant refactoring challenge with no direct Java equivalent.

6. **Batch-Online Data Sharing:** `ACCTDATA` and `TRANSACT` are written by both batch (CBTRN02C, CBACT04C) and online (COACTUPC, COBIL00C, COTRN02C) programs — creating dual-write consistency concerns during migration.

7. **No CI/CD, No Test Suite, No Build System:** The repo has no automated testing or build infrastructure. GnuCOBOL can compile batch programs; CICS programs require IBM CICS runtime or AWS M2.
