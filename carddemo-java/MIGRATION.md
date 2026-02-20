# CardDemo Mainframe to Java Migration Documentation

## Overview
This document describes the migration of the CardDemo mainframe COBOL/CICS application to a modern Java/Spring Boot architecture.

## Architecture Mapping

### Data Layer (VSAM -> PostgreSQL/H2)
| VSAM File | COBOL Copybook | Java Entity | Database Table |
|-----------|---------------|-------------|----------------|
| CUSTOMER-FILE | CVCUS01Y | Customer | customers |
| ACCOUNT-FILE | CVACT01Y | Account | accounts |
| CARD-FILE | CVACT02Y | Card | cards |
| XREF-FILE | CVACT03Y | CardXref | card_xref |
| TRANSACT-FILE | CVTRA05Y | Transaction | transactions |
| TCATBAL-FILE | CVTRA01Y | TranCatBalance | tran_cat_balances |
| DISCGRP-FILE | CVTRA02Y | DisclosureGroup | disclosure_groups |
| USRSEC-FILE | CSUSR01Y | UserSecurity | user_security |

### Batch Processing (COBOL Batch -> Spring Batch)
| COBOL Program | Function | Spring Batch Job |
|--------------|----------|-----------------|
| CBTRN01C | Daily Transaction Posting | dailyTransactionPostingJob |
| CBACT04C | Interest Calculation | interestCalculationJob |

### Online Processing (CICS -> REST APIs)
| CICS Program | Transaction | REST Endpoint |
|-------------|------------|---------------|
| COUSR01C | CU01 - Add User | POST /api/users |
| COTRN01C | CT01 - View Transaction | GET /api/transactions/{id} |
| COACTVWC | CA01 - View Account | GET /api/accounts/{id} |

### UI (BMS Screens -> React)
| BMS Map | Screen | React Component |
|---------|--------|----------------|
| COACTVW | Account Viewer | AccountViewer.js |
| COTRN01 | Transaction View | TransactionViewer.js |
| COUSR01 | User Add | UserManagement.js |

## Business Logic Preservation

### CBTRN01C - Daily Transaction Posting
- **Original**: Reads daily transaction file sequentially, validates card via XREF lookup, verifies account exists, posts valid transactions
- **Migrated**: Spring Batch chunk-oriented step with JPA reader, processor (XREF + account validation), and writer
- **Key validation**: Card number lookup in card_xref table (equivalent to XREF-FILE READ with INVALID KEY)
- **Skip logic**: Transactions with invalid card numbers are filtered (processor returns null)

### CBACT04C - Interest Calculation
- **Original**: Reads transaction category balances, computes interest per formula: `MONTHLY-INT = (TRAN-CAT-BAL * DIS-INT-RATE) / 1200`
- **Migrated**: Spring Batch tasklet that iterates accounts, looks up disclosure group rates, computes interest, writes transaction records
- **Interest formula preserved**: `monthlyInt = balance * intRate / 1200` (BigDecimal arithmetic)
- **Transaction generation**: Type '01', Category '05', Source 'System', ID format: parmDate + 6-digit suffix
- **Account update**: Adds total interest to current balance, resets cycle credit/debit to zero

### COUSR01C - User Management
- **Original**: CICS program with field validation (fname, lname, userId, password, userType not empty), duplicate check via WRITE with DUPKEY/DUPREC response
- **Migrated**: REST API with identical validation rules and DuplicateResourceException for existing user IDs

### COTRN01C - Transaction Viewing
- **Original**: CICS READ of TRANSACT file by RIDFLD (transaction ID), displays all fields including merchant info
- **Migrated**: REST API GET by transaction ID with ResourceNotFoundException for missing records

## File Status Code Mapping
| COBOL Status | Meaning | Java Equivalent |
|-------------|---------|----------------|
| '00' | Success | Entity returned from repository |
| '10' | End of file | Empty result / end of pagination |
| '12' | Error | RuntimeException |
| '23' | Record not found | Optional.empty() / ResourceNotFoundException |
| DUPKEY/DUPREC | Duplicate | DuplicateResourceException |

## Running the Application

### Development (H2)
```bash
cd carddemo-java
./mvnw spring-boot:run
```

### Production (PostgreSQL)
```bash
cd carddemo-java
./mvnw spring-boot:run -Dspring-boot.run.profiles=postgres
```

### Running Batch Jobs via REST
```bash
# Daily Transaction Posting
curl -X POST http://localhost:8080/api/batch/daily-transaction-posting

# Interest Calculation
curl -X POST http://localhost:8080/api/batch/interest-calculation?parmDate=2024-01-15
```

### Frontend
```bash
cd carddemo-java/frontend
npm install
npm start
```

## API Reference
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/accounts/{id} | View account with customer details |
| GET | /api/accounts | List all accounts |
| GET | /api/transactions/{id} | View transaction by ID |
| GET | /api/transactions?page=0&size=10 | List transactions (paginated) |
| GET | /api/transactions/by-card/{cardNum} | Transactions by card number |
| POST | /api/users | Add new user |
| GET | /api/users | List all users |
| GET | /api/users/{id} | Get user by ID |
| PUT | /api/users/{id} | Update user |
| DELETE | /api/users/{id} | Delete user |
| GET | /api/cards/{cardNum} | View card details |
| GET | /api/cards/by-account/{acctId} | Cards by account |
| GET | /api/cards/xref/{cardNum} | Card cross-reference lookup |
| POST | /api/batch/daily-transaction-posting | Run transaction posting job |
| POST | /api/batch/interest-calculation | Run interest calculation job |
