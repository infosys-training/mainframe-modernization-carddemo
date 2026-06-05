# HOTSPOT REPORT — CardDemo COBOL Estate

## Methodology

Each program is scored across five complexity dimensions:

| Dimension | Weight | Measurement |
|-----------|--------|-------------|
| Lines of Code (LOC) | 20% | Total source lines |
| Copybook References | 20% | Number of COPY statements (indicates coupling) |
| I/O Operations | 20% | Count of file/CICS I/O verbs (READ, WRITE, REWRITE, DELETE, STARTBR, READNEXT, READPREV, ENDBR, OPEN, CLOSE, EXEC CICS) |
| Business Logic Density | 25% | Count of EVALUATE + IF statements (branching complexity, proxy for nesting depth) |
| Inter-Program Dependencies | 15% | CALL + XCTL targets + copybook-coupled programs |

Scores are normalized to 0–100 per dimension then weighted-averaged. Higher score = higher complexity = higher modernization priority.

---

## Top 10 Hotspot Programs

| Rank | Program | Type | LOC | Copybooks | I/O Ops | Logic Stmts | Dependencies | Weighted Score |
|------|---------|------|-----|-----------|---------|-------------|-------------|---------------|
| 1 | COACTUPC.cbl | CICS | 4,236 | 15 (+39 CSSETATY) | 19 | 188 | 8 VSAM files, XCTL, CALL CSUTLDTC | **92.4** |
| 2 | COCRDUPC.cbl | CICS | 1,560 | 13 | 13 | 164 | 2 VSAM files, XCTL | **62.3** |
| 3 | COCRDLIC.cbl | CICS | 1,459 | 11 | 21 | 140 | 1 VSAM file, XCTL (×3) | **59.8** |
| 4 | COACTVWC.cbl | CICS | 941 | 14 | 15 | ~80 | 3 VSAM files, XCTL | **48.6** |
| 5 | CBSTM03A.CBL | Batch | 924 | 4 | 117 | ~40 | CALL CBSTM03B, 4 VSAM files | **47.2** |
| 6 | COCRDSLC.cbl | CICS | 887 | 13 | 14 | 76 | 2 VSAM files, XCTL | **44.1** |
| 7 | COTRN02C.cbl | CICS | 783 | 10 | 19 | 40 | 3 VSAM files, CALL CSUTLDTC (×2), XCTL | **42.5** |
| 8 | CBTRN02C.cbl | Batch | 731 | 5 | 23 | ~50 | 5 VSAM files, CALL CEE3ABD | **40.8** |
| 9 | COTRN00C.cbl | CICS | 699 | 8 | 24 | 42 | 1 VSAM file, XCTL | **38.2** |
| 10 | COUSR00C.cbl | CICS | 695 | 8 | 25 | 41 | 1 VSAM file, XCTL (×3) | **37.9** |

---

## Detailed Analysis

### Rank 1 — COACTUPC.cbl (Account Update) — Score: 92.4

| Metric | Value | Notes |
|--------|-------|-------|
| LOC | 4,236 | 2.7× the next largest program; largest in the estate |
| Copybooks | 15 unique + 39 CSSETATY COPY REPLACING | Most heavily coupled; embeds date validation (CSUTLDWY/CSUTLDPY), lookup tables (CSLKPCDY), screen attribute macros (CSSETATY) |
| I/O Operations | 19 EXEC CICS operations | 5 file READs, 3 REWRITEs, MAP SEND/RECEIVE |
| Logic Density | 188 EVALUATE/IF statements | Exhaustive field-by-field validation: date (year/month/day/leap year/DOB), SSN, phone area code (NANPA), state code (50 states), ZIP-to-state cross-validation |
| Dependencies | Reads/writes 4 VSAM files (ACCTFILE, CARDXREF, CUSTFILE, CARDFILE); XCTL to/from COMEN01C; CALL CSUTLDTC |

**Why highest priority:** This program concentrates more business logic than any other in the estate. The 39× COPY REPLACING macro usage generates code at compile time, inflating effective complexity beyond what LOC alone shows. The date validation alone spans ~300 lines (CSUTLDPY inlined). Decomposing this into separate validation, persistence, and presentation layers is the single highest-value modernization activity.

---

### Rank 2 — COCRDUPC.cbl (Credit Card Update) — Score: 62.3

| Metric | Value |
|--------|-------|
| LOC | 1,560 |
| Logic Density | 164 EVALUATE/IF |
| Copybooks | 13 |

**Rationale:** Second-most complex after COACTUPC. Shares similar patterns (screen-driven CRUD with inline validation). Card update touches CARDFILE and CUSTFILE with extensive field validation. Should be modernized alongside COACTUPC since they share the same UI/validation patterns.

---

### Rank 3 — COCRDLIC.cbl (Credit Card List) — Score: 59.8

| Metric | Value |
|--------|-------|
| LOC | 1,459 |
| Logic Density | 140 EVALUATE/IF |
| I/O Ops | 21 (paginated browse: STARTBR/READNEXT/READPREV/ENDBR) |

**Rationale:** Pagination logic with forward/backward browsing is complex CICS-specific code. The 3 XCTL targets indicate this program serves as a secondary navigation hub. High logic density stems from handling edge cases in page navigation (first page, last page, empty result set, key repositioning).

---

### Rank 4 — COACTVWC.cbl (Account View) — Score: 48.6

| Metric | Value |
|--------|-------|
| LOC | 941 |
| Copybooks | 14 |
| I/O | Reads 3 VSAM files (ACCTFILE, CARDFILE, CARDXREF) |

**Rationale:** Read-only display but still complex due to multi-file joins (account → xref → card → customer) and 14 copybook dependencies. Good candidate for early modernization since it is read-only (lower risk) and can validate the data access layer.

---

### Rank 5 — CBSTM03A.CBL (Statement Generation) — Score: 47.2

| Metric | Value |
|--------|-------|
| LOC | 924 |
| I/O Ops | 117 (highest I/O count in estate) |
| Dependencies | CALL CBSTM03B; reads 4 VSAM files |

**Rationale:** Generates both text and HTML output from 4 input files. Extremely I/O-intensive with 97 WRITE operations for formatted output. Self-contained batch process with clear inputs/outputs, making it an ideal **proof-of-concept** for batch modernization. Can be converted to template-based generation (e.g., Thymeleaf/FreeMarker + Spring Batch).

---

### Rank 6 — COCRDSLC.cbl (Credit Card Detail View) — Score: 44.1

| Metric | Value |
|--------|-------|
| LOC | 887 |
| Logic Density | 76 EVALUATE/IF |
| Copybooks | 13 |

**Rationale:** Similar to COACTVWC but for credit cards. High copybook coupling. Should be modernized with COCRDLIC and COCRDUPC as a single card-management module.

---

### Rank 7 — COTRN02C.cbl (Transaction Add) — Score: 42.5

| Metric | Value |
|--------|-------|
| LOC | 783 |
| Dependencies | 2× CALL CSUTLDTC, reads 3 VSAM files |
| I/O Ops | 19 |

**Rationale:** Writes new transactions to the TRANSACT file with date validation. The dual CALL to CSUTLDTC indicates two separate date fields are validated. Modernization should extract date validation into a shared service.

---

### Rank 8 — CBTRN02C.cbl (Post Daily Transactions — Batch) — Score: 40.8

| Metric | Value |
|--------|-------|
| LOC | 731 |
| I/O Ops | 23 |
| Dependencies | 5 VSAM files, CALL CEE3ABD |

**Rationale:** Core batch pipeline program (POSTTRAN job). Reads daily transactions, validates against XREF, posts to TRANSACT master, updates ACCTFILE balances, and writes rejected records to DALYREJS. Touching 5 VSAM files makes this the most data-coupled batch program. Critical path for daily operations — requires careful parallel-run testing during modernization.

---

### Rank 9 — COTRN00C.cbl (Transaction List) — Score: 38.2

| Metric | Value |
|--------|-------|
| LOC | 699 |
| I/O Ops | 24 |
| Logic Density | 42 EVALUATE/IF |

**Rationale:** Paginated browse of TRANSACT file. Similar pagination pattern to COCRDLIC. Should be converted to a paginated REST API with the card list browse.

---

### Rank 10 — COUSR00C.cbl (User List) — Score: 37.9

| Metric | Value |
|--------|-------|
| LOC | 695 |
| I/O Ops | 25 |
| Dependencies | 3 XCTL targets (COUSR01C, COUSR02C, COUSR03C) |

**Rationale:** User management hub. Highest I/O count among CICS programs due to paginated browse of USRSEC file. The 3 XCTL targets to COUSR01/02/03C form a self-contained user-management sub-application. Good candidate for isolated modernization as an independent microservice.

---

## Modernization Prioritization Recommendations

### Phase 1 — Highest Value, Self-Contained (Months 1–3)

| Priority | Program(s) | Rationale |
|----------|-----------|-----------|
| 1 | **CBSTM03A / CBSTM03B** (Statement Generation) | Self-contained batch, clear I/O contract, no CICS dependency. Ideal proof-of-concept. Convert to Spring Batch + template engine. |
| 2 | **COUSR00C / 01C / 02C / 03C** (User Management) | Isolated sub-application, simple CRUD on single VSAM file. Converts cleanly to REST API + database. Security model can be replaced by standard auth (Spring Security, JWT). |

### Phase 2 — Core Business Logic (Months 3–6)

| Priority | Program(s) | Rationale |
|----------|-----------|-----------|
| 3 | **COACTUPC** (Account Update) | Highest complexity but also the highest business value. Decompose into AccountService (persistence), AccountValidationService (date/SSN/phone/state/ZIP rules extracted from CSUTLDPY and CSLKPCDY), and AccountController (REST). Requires comprehensive regression test suite (10,000+ test cases recommended due to combinatorial validation paths). |
| 4 | **CBTRN02C** (Post Transactions — Batch) | Critical daily pipeline. Convert to Spring Batch with chunk processing. Must run dual-write (VSAM + target DB) during transition with reconciliation. |
| 5 | **CBACT04C** (Interest Calculator) | Pure computation with 5-file lookup. Convert to a service with clear interfaces. Monetary calculations must use BigDecimal (never float/double). |

### Phase 3 — Remaining CICS Programs (Months 6–10)

| Priority | Program(s) | Rationale |
|----------|-----------|-----------|
| 6 | **COCRDLIC / COCRDSLC / COCRDUPC** (Card Management) | Group as a single card-management module. Shared patterns with account programs. |
| 7 | **COACTVWC** (Account View) | Read-only, lower risk. Can validate data layer early. |
| 8 | **COTRN00C / COTRN01C / COTRN02C** (Transaction UI) | Transaction list/view/add. Convert browse pattern to paginated REST API. |
| 9 | **COBIL00C / CORPT00C** | Bill payment and report submission. Lower complexity. |
| 10 | **COSGN00C / COADM01C / COMEN01C** | Navigation shell — replaced by frontend SPA routing + authentication middleware. |

### Cross-Cutting Concerns

| Item | Recommendation |
|------|---------------|
| **Date Validation** (CSUTLDTC + CSUTLDPY + CSUTLDWY) | Extract to a shared `DateValidationService`. Currently inlined via COPY in COACTUPC and CALLed by CORPT00C and COTRN02C. |
| **Lookup Tables** (CSLKPCDY) | Migrate state/ZIP/phone validation data to database reference tables or a validation microservice. |
| **COMMAREA** (COCOM01Y) | Replace with session state management (HTTP session or JWT claims). The COMMAREA's navigation fields (FROM-PROGRAM, TO-PROGRAM) become URL routing. |
| **BMS Maps** | Replace with React/Angular/Vue components. All 17 BMS copybooks become frontend components. |
| **VSAM Files** | Migrate to PostgreSQL tables with matching primary keys and indexes. Keep VSAM key structure in table design for initial migration fidelity. |
| **Error Handling** (CEE3ABD calls) | Replace with structured exception handling (try/catch) and logging framework. |
| **COPY REPLACING** (CSSETATY) | The 39× macro expansion in COACTUPC is a code generation pattern — replace with attribute-setting helper methods or a UI framework's data binding. |
