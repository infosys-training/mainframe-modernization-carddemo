# CardDemo COBOL-to-Microservices Migration Plan

## Executive Summary

This document describes the migration of the CardDemo mainframe application (COBOL/CICS/VSAM) to a Java Spring Boot microservices architecture. The CardDemo system is a credit card management application consisting of **28 COBOL programs** (~19,254 LOC), **28 copybooks**, **16 BMS screen maps**, and **46 JCL batch jobs**.

## Source Application Analysis

### COBOL Estate Metrics
| Category | Count | Details |
|----------|-------|---------|
| Online CICS programs | 21 | Screen-driven, pseudo-conversational |
| Batch programs | 7 | Sequential file processing |
| Copybooks | 28 | Shared data structures |
| BMS screen maps | 16 | 3270 terminal UI definitions |
| JCL jobs | 46 | Batch orchestration |
| Total LOC | 19,254 | Across all 28 programs |
| Largest program | COACTUPC.cbl | 4,236 LOC |

### Technology Stack (Source)
| Component | Technology | Migration Target |
|-----------|-----------|-----------------|
| Language | COBOL | Java 21 |
| Online runtime | CICS | Spring Boot REST APIs |
| File system | VSAM KSDS | PostgreSQL / H2 |
| Screen maps | BMS | REST API + Modern UI |
| Job control | JCL | Spring Batch |
| Messaging | MQ Series | REST / SQS (future) |
| Hierarchical DB | IMS DL/I | JPA + Relational DB |
| Security | RACF | Spring Security + BCrypt |

## Target Architecture: 8 Microservices

### Service Decomposition

```
┌─────────────────────────────────────────────────────────────┐
│                      API Gateway                             │
│                   (future: Spring Cloud Gateway)             │
└────┬──────┬──────┬──────┬──────┬──────┬──────┬──────┬───────┘
     │      │      │      │      │      │      │      │
┌────┴─┐┌───┴──┐┌──┴───┐┌─┴────┐┌┴─────┐┌┴─────┐┌┴─────┐┌──┴───┐
│Acct  ││Card  ││Trans ││User  ││Report││Tran  ││Auth  ││Data  │
│Svc   ││Svc   ││Svc   ││Auth  ││Svc   ││Type  ││Svc   ││Migr  │
│:8081 ││:8082 ││:8083 ││:8084 ││:8085 ││:8086 ││:8087 ││:8088 │
└──┬───┘└──┬───┘└──┬───┘└──┬───┘└──┬───┘└──┬───┘└──┬───┘└──┬───┘
   └───────┴───────┴───────┴───────┴───────┴───────┴───────┘
                        PostgreSQL (schema-per-service)
```

### Service-to-COBOL Mapping

| Microservice | COBOL Programs | LOC | Port | Function |
|---|---|---|---|---|
| **account-service** | COACTUPC, COACTVWC | 5,177 | 8081 | Account & customer CRUD |
| **card-service** | COCRDLIC, COCRDSLC, COCRDUPC | 3,906 | 8082 | Credit card management |
| **transaction-service** | COTRN00C/01C/02C, CBTRN01C/02C, COBIL00C | 3,920 | 8083 | Transaction processing |
| **user-auth-service** | COSGN00C, COUSR00C-03C, COMEN01C, COADM01C | 3,897 | 8084 | Authentication & user CRUD |
| **reporting-service** | CBSTM03A/B, CBTRN03C, CORPT00C | 2,210 | 8085 | Statement & report generation |
| **transaction-type-service** | COTRTLIC, COTRTUPC | 3,800 | 8086 | Transaction type/category CRUD |
| **authorization-service** | COPAUA0C, COPAUS0C-2C, CBPAUP0C | 3,640 | 8087 | Payment authorization & fraud detection |
| **data-migration-tool** | CBEXPORT, CBIMPORT | 1,069 | 8088 | VSAM→RDBMS data migration |

### Shared Libraries
- **carddemo-common** — Shared entities, DTOs, exception handling, validation, security config

## Data Model: VSAM → Relational

### Entity Mapping (Copybook → JPA Entity)

| Copybook | Record Length | Entity | Table | Key |
|----------|-------------|--------|-------|-----|
| CVACT01Y | 300 bytes | Account | accounts | acct_id (PIC 9(11)) |
| CVCUS01Y | 500 bytes | Customer | customers | cust_id (PIC 9(09)) |
| CVACT02Y | 150 bytes | Card | cards | card_num (PIC X(16)) |
| CVACT03Y | 50 bytes | CardXref | card_xref | xref_card_num |
| CVTRA05Y | 350 bytes | Transaction | transactions | tran_id (PIC X(16)) |
| CVTRA03Y | 60 bytes | TransactionType | transaction_types | tran_type (PIC X(02)) |
| CVTRA04Y | 60 bytes | TransactionCategory | transaction_categories | (type_cd, cat_cd) |
| CVTRA01Y | 50 bytes | TransactionCategoryBalance | transaction_category_balances | (acct_id, type, cat) |
| CVTRA02Y | 50 bytes | DisclosureGroup | disclosure_groups | (group_id, type, cat) |
| CSUSR01Y | 80 bytes | UserSecurity | user_security | usr_id (PIC X(08)) |

### Data Relationships
```
Customer (CVCUS01Y) ──1:N──► CardXref (CVACT03Y) ──N:1──► Account (CVACT01Y)
                                    │
                              Card (CVACT02Y)
                                    │
                         Transaction (CVTRA05Y) ──► TransactionType (CVTRA03Y)
                                                   ──► TransactionCategory (CVTRA04Y)
```

## Key Migration Patterns

### 1. BMS Screens → REST APIs
Each CICS program's BMS screen becomes a REST endpoint:
- SEND MAP / RECEIVE MAP → JSON request/response
- PF key navigation → HTTP methods (GET/POST/PUT/DELETE)
- COMMAREA data passing → DTO objects

### 2. VSAM File Operations → JPA Repository
- READ → `repository.findById()`
- WRITE/REWRITE → `repository.save()`
- STARTBR/READNEXT/READPREV/ENDBR → `repository.findAll(Pageable)`
- DELETE → `repository.delete()`

### 3. COBOL Signed Decimal → Java BigDecimal
All PIC S9(n)V99 fields use `java.math.BigDecimal` with scale=2.
Overpunch encoding ({, A-I for positive; }, J-R for negative) handled by `CobolRecordParser`.

### 4. Plain-Text Passwords → BCrypt
CSUSR01Y stores PIC X(08) passwords in clear text.
Migration includes dual-hash strategy: verify BCrypt first, fall back to plain-text,
then rehash on successful legacy login.

### 5. IMS/MQ → REST APIs
- IMS DL/I calls (GU, GN, GNP) → JPA queries with parent-child joins
- MQ (MQOPEN, MQGET, MQPUT1) → REST API calls between services
- Future: Amazon SQS for async authorization flow

## Upstream/Downstream Dependencies

> **IMPORTANT** (per COBOL migration policy): Dependencies with other systems must be flagged before migration.

| Dependency | Type | Impact | Mitigation |
|-----------|------|--------|------------|
| MQ message consumers | Downstream | Authorization messages consumed by external systems | Implement REST adapter; run parallel MQ/REST during transition |
| IMS database shared with other apps | Upstream/Downstream | IMS hierarchy maps to 2 relational tables | Migrate data; provide API for external consumers |
| JCL batch scheduling (Control-M) | Operational | Batch jobs triggered by external scheduler | Spring Batch + scheduled tasks; expose batch trigger API |
| VSAM files shared across programs | Internal | Multiple services write to same VSAM files | Schema-per-service with clear data ownership |
| Assembler subroutines (COBDATFT, MVSWAIT) | Internal | Date formatting and wait routines | Java utility replacements (`DateTimeFormatter`, `Thread.sleep`) |

## Risk Register

| # | Risk | Severity | Mitigation |
|---|------|----------|------------|
| 1 | COACTUPC complexity (4,236 LOC, 359 branches) | HIGH | Split into AccountService + ValidationService; extensive testing |
| 2 | IMS dependency (7 programs) | MEDIUM | Map IMS hierarchy to relational; migrate as unit |
| 3 | MQ integration (4 programs) | MEDIUM | REST adapter with parallel MQ/REST period |
| 4 | BMS screen coupling (20 programs) | MEDIUM | Extract business logic; separate API from UI |
| 5 | COBOL fixed-point arithmetic | HIGH | BigDecimal mandatory for all monetary fields |
| 6 | Plain-text passwords | HIGH | BCrypt with dual-hash migration strategy |
| 7 | GDG file versioning | LOW | Timestamped file naming in batch jobs |
| 8 | Data consistency during dual-write | HIGH | FIFO queues for sync; hourly reconciliation |

## Implementation Status

### Sprint 1: Foundation (Complete)
- [x] Multi-module Maven project with 9 modules
- [x] Shared entity library with 10 JPA entities
- [x] Common DTO, exception handling, validation
- [x] Spring Security with BCrypt password encoding

### Sprint 2: Core Services (Complete)
- [x] Account Service — account & customer CRUD, cross-reference
- [x] Card Service — card management with activation/deactivation
- [x] User Auth Service — login, user CRUD, legacy password migration
- [x] Transaction Type Service — type & category CRUD with cascading deletes

### Sprint 3: Processing Services (Complete)
- [x] Transaction Service — create, process, batch posting
- [x] Authorization Service — velocity checks, amount limits, fraud detection
- [x] Reporting Service — statement generation, daily reports
- [x] Data Migration Tool — COBOL record parser with overpunch encoding

### Sprint 4: Testing (Complete)
- [x] 37 unit tests across all services — all passing
- [x] Account service tests (business rules, validation, cross-reference)
- [x] User auth tests (BCrypt hashing, legacy migration, user management)
- [x] Authorization tests (velocity limits, amount limits, fraud alerts)
- [x] COBOL record parser tests (overpunch encoding, decimal parsing)
- [x] Validation utility tests (state codes, SSN, FICO, dates)

## Future Phases

### Phase 5: API Gateway & Service Discovery
- Spring Cloud Gateway for unified entry point
- Service registry (Eureka or Consul)
- Distributed tracing (Zipkin/Jaeger)

### Phase 6: Data Migration
- Run CobolRecordParser against production EBCDIC data
- Flyway DDL migrations for PostgreSQL
- Data validation and reconciliation

### Phase 7: Batch Pipeline Migration
- Spring Batch jobs replacing JCL: POSTTRAN → INTCALC → CREASTMT → TRANRPT
- Scheduled execution via Spring Scheduler or AWS Step Functions

### Phase 8: Decommission
- 30-day parallel run
- Traffic cutover from mainframe to microservices
- VSAM file decommission after stability period
