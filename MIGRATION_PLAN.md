# CardDemo Mainframe Modernization — Migration Plan

## Overview

This document describes the plan to convert the CardDemo mainframe application (COBOL + CICS BMS) into a modern Python backend (FastAPI) with a React frontend. The application is a credit card management system with online CICS transactions and batch processing.

## Architecture Mapping

| Mainframe Layer | Modern Equivalent |
|---|---|
| COBOL online programs (`app/cbl/CO*.cbl`) | Python REST API (FastAPI) |
| COBOL batch programs (`app/cbl/CB*.cbl`) | Python batch scripts/services |
| BMS screen maps (`app/bms/*.bms`) | React components |
| VSAM files | PostgreSQL |
| Copybooks (`app/cpy/*.cpy`) | Python dataclasses / SQLAlchemy models |
| CICS COMMAREA | API request/response JSON |
| JCL jobs (`app/jcl/`) | Orchestration (Celery, cron, Airflow) |

## Program-to-Module Mapping

| COBOL Program | Function | Python/React Target |
|---|---|---|
| `COSGN00C.cbl` | Sign-on | Auth API + Login React page |
| `COMEN01C.cbl` | Main menu | React navigation/router |
| `COADM01C.cbl` | Admin menu | React admin layout |
| `COACTVWC.cbl` | Account view | Account API + React component |
| `COACTUPC.cbl` | Account update | Account update API + form |
| `COTRN00C.cbl`–`COTRN02C.cbl` | Transaction list/add | Transaction API + React pages |
| `COCRDLIC.cbl`/`COCRDSLC.cbl`/`COCRDUPC.cbl` | Card list/select/update | Card API + React pages |
| `COUSR00C.cbl`–`COUSR03C.cbl` | User admin | User admin API + React pages |
| `COBIL00C.cbl` | Billing | Billing API + React page |
| `CORPT00C.cbl` | Reports | Reports API + React page |
| `CBTRN01C.cbl`–`CBTRN03C.cbl` | Batch transaction posting | Python batch services |
| `CBACT01C.cbl`–`CBACT04C.cbl` | Batch account/interest calc | Python batch services |
| `CBSTM03A.CBL`/`CBSTM03B.CBL` | Statement generation | Python batch service |

---

## Phase 1: Database Layer (replaces VSAM)

1. Create a PostgreSQL schema based on the copybooks in `app/cpy/`:
   - `CSUSR01Y.cpy` → `users` table (user_id, password, first_name, last_name, user_type)
   - `CVACT01Y.cpy` → `accounts` table (account_id, status, current_balance, credit_limit, etc.)
   - `CVACT02Y.cpy` / `CVACT03Y.cpy` → additional account detail tables
   - `CVCRD01Y.cpy` → `cards` table (card_number, account_id, card_status, expiry_date)
   - `CVCUS01Y.cpy` → `customers` table (customer_id, name, address, phone, etc.)
   - `CVTRA05Y.cpy` (and CVTRA01Y–07Y) → `transactions` table (transaction_id, card_number, amount, timestamp, category, etc.)
   - `CUSTREC.cpy` → customer cross-reference
2. Create SQLAlchemy models matching these tables.
3. Write a data migration script that loads the seed data from `app/data/ASCII/` into the new database.

## Phase 2: Python Backend API (replaces COBOL online programs)

Create a FastAPI application with the following modules:

### Auth module (replaces `COSGN00C.cbl`)
- `POST /api/auth/login` — validate user credentials (was USRSEC VSAM read)
- `POST /api/auth/logout`
- JWT token-based authentication (replaces CICS COMMAREA session)
- Role-based access: "admin" vs "regular user" (the COBOL checks `SEC-USR-TYPE`)

### Account module (replaces `COACTVWC.cbl` and `COACTUPC.cbl`)
- `GET /api/accounts/:id` — view account details
- `PUT /api/accounts/:id` — update account
- `GET /api/accounts` — list accounts

### Card module (replaces `COCRDLIC.cbl`, `COCRDSLC.cbl`, `COCRDUPC.cbl`)
- `GET /api/cards` — list cards (with pagination)
- `GET /api/cards/:id` — card detail
- `PUT /api/cards/:id` — update card

### Transaction module (replaces `COTRN00C.cbl`, `COTRN01C.cbl`, `COTRN02C.cbl`)
- `GET /api/transactions` — list transactions
- `POST /api/transactions` — add transaction

### User Admin module (replaces `COUSR00C.cbl` through `COUSR03C.cbl`)
- CRUD endpoints for `/api/users` — admin-only

### Reports & Billing (replaces `CORPT00C.cbl`, `COBIL00C.cbl`)
- `GET /api/reports/transactions` — transaction report
- `GET /api/billing/statements` — billing information

## Phase 3: Python Batch Services (replaces COBOL batch programs)

Create Python scripts/services in a `batch/` directory:

- `batch/transaction_posting.py` — replaces `CBTRN01C.cbl`, `CBTRN02C.cbl`, `CBTRN03C.cbl`
  - Reads unposted transactions, validates, updates account balances
- `batch/interest_calculation.py` — replaces `CBACT04C.cbl`
  - Calculates monthly interest on accounts
- `batch/account_processing.py` — replaces `CBACT01C.cbl`, `CBACT02C.cbl`, `CBACT03C.cbl`
  - Batch account file operations
- `batch/statement_generation.py` — replaces `CBSTM03A.CBL`, `CBSTM03B.CBL`
  - Generates monthly statements
- `batch/customer_processing.py` — replaces `CBCUS01C.cbl`
  - Batch customer data processing

Use Celery or APScheduler for scheduling (replaces JCL job scheduling from `app/jcl/`).

## Phase 4: React Frontend (replaces BMS maps)

Create a React application (Vite + React Router + Material UI or Ant Design):

### Pages (each maps to a BMS screen map):
- `src/pages/Login.tsx` — replaces `COSGN00.bms`
- `src/pages/MainMenu.tsx` — replaces `COMEN01.bms`
- `src/pages/AdminMenu.tsx` — replaces `COADM01.bms`
- `src/pages/AccountView.tsx` — replaces `COACTVW.bms`
- `src/pages/AccountUpdate.tsx` — replaces `COACTUP.bms`
- `src/pages/CardList.tsx` — replaces `COCRDLI.bms`
- `src/pages/CardSelect.tsx` — replaces `COCRDSL.bms`
- `src/pages/CardUpdate.tsx` — replaces `COCRDUP.bms`
- `src/pages/TransactionList.tsx` — replaces `COTRN00.bms` and `COTRN01.bms`
- `src/pages/TransactionAdd.tsx` — replaces `COTRN02.bms`
- `src/pages/UserList.tsx` — replaces `COUSR00.bms`
- `src/pages/UserAdd.tsx` — replaces `COUSR01.bms`
- `src/pages/UserUpdate.tsx` — replaces `COUSR02.bms`
- `src/pages/UserDelete.tsx` — replaces `COUSR03.bms`
- `src/pages/Reports.tsx` — replaces `CORPT00.bms`
- `src/pages/Billing.tsx` — replaces `COBIL00.bms`

### Routing:
- Unauthenticated → Login page
- After login, route based on user type:
  - Admin → Admin menu (with access to user management)
  - Regular user → Main menu
- Mirrors the COBOL logic in `COSGN00C` that checks user type and XCTLs to either `COADM01C` or `COMEN01C`

### Shared components:
- Header (replaces the standard BMS header)
- Error message display (replaces ERRMSGO field)
- Navigation bar (replaces PF-key navigation)
- Pagination controls (replaces forward/backward paging logic)

### State management:
- React Context or Redux for auth state (replaces COMMAREA)
- API calls using Axios or fetch

## Phase 5: Target Project Structure

```
carddemo-modern/
├── backend/
│   ├── app/
│   │   ├── main.py            # FastAPI app entry
│   │   ├── models/            # SQLAlchemy models (from copybooks)
│   │   ├── routers/           # API route modules
│   │   │   ├── auth.py
│   │   │   ├── accounts.py
│   │   │   ├── cards.py
│   │   │   ├── transactions.py
│   │   │   ├── users.py
│   │   │   ├── reports.py
│   │   │   └── billing.py
│   │   ├── services/          # Business logic
│   │   ├── schemas/           # Pydantic request/response schemas
│   │   └── database.py        # DB connection
│   ├── batch/                 # Batch processing scripts
│   ├── migrations/            # Alembic migrations
│   ├── tests/
│   └── requirements.txt
├── frontend/
│   ├── src/
│   │   ├── pages/             # React pages (one per BMS map)
│   │   ├── components/        # Shared components
│   │   ├── services/          # API client functions
│   │   ├── context/           # Auth context
│   │   └── App.tsx            # Router setup
│   ├── package.json
│   └── vite.config.ts
├── database/
│   └── schema.sql             # Initial schema
└── docker-compose.yml         # PostgreSQL + backend + frontend
```

## Key Conversion Notes

1. **CICS EXEC commands** (SEND MAP, RECEIVE MAP, XCTL, RETURN) → API request/response cycles and React Router navigation.
2. **COMMAREA** data passing → JWT claims + API payloads.
3. **PF keys** (PF3=Exit, PF7/PF8=Page) → React UI buttons and keyboard shortcuts.
4. **VSAM READ/WRITE/REWRITE** → SQLAlchemy queries.
5. **COPY statements** referencing copybooks → Python imports of model/schema classes.
6. **88-level condition names** in COBOL → Python enums or constants.
7. **BMS field attributes** (ASKIP, PROT, NUM) → HTML input attributes (disabled, readonly, type="number").

## Implementation Order

1. Database schema + models (from copybooks)
2. Auth API + Login React page (prove end-to-end flow works)
3. Account view/update API + React pages
4. Transaction APIs + React pages
5. Card management APIs + React pages
6. User admin APIs + React pages
7. Reports & billing
8. Batch processing services
9. Integration testing
10. Data migration from seed files
