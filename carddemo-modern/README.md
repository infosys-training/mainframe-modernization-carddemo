# CardDemo Modern

Modernized version of the CardDemo mainframe application (COBOL + CICS BMS + VSAM)
rebuilt as a **Python FastAPI backend** and a **React (Vite + TypeScript + MUI)
frontend**, backed by **PostgreSQL**.

- CICS online programs → FastAPI REST endpoints
- BMS 3270 maps → React pages
- VSAM files → PostgreSQL tables (SQLAlchemy models from the COBOL copybooks)
- COMMAREA session state → JWT authentication
- JCL batch scheduling → Python batch services + APScheduler

## Quick start

Requirements: Docker, plus `python3` and `node`/`npm` if running the scripts natively.

### Option A — Docker Compose (everything in containers)

```bash
cd carddemo-modern
docker-compose up --build
```

### Option B — local scripts

```bash
cd carddemo-modern
./setup.sh     # first time only: installs deps, starts PostgreSQL, loads seed data
./run.sh       # starts backend + frontend (Ctrl-C to stop)
```

Then open:

| Service        | URL                              |
|----------------|----------------------------------|
| Frontend       | http://localhost:5173            |
| Backend API    | http://localhost:8000            |
| API docs       | http://localhost:8000/docs       |
| Health check   | http://localhost:8000/api/health |

## Login credentials

Two seed users are loaded from the original CardDemo security file. Use them to
see the **role-based** screens described below.

| Role         | User ID    | Password   | `user_type` |
|--------------|------------|------------|-------------|
| Admin        | `admin`    | `admin123` | `A`         |
| Regular user | `user0001` | `user1234` | `U`         |

> Tip: clear the User ID field before typing (browser autofill can insert an
> extra value), and hard-refresh (Ctrl+Shift+R) if a stale session lingers.

## Role-based access (who sees what)

This mirrors the COBOL sign-on logic in `COSGN00C`, which routes admins to the
admin menu (`COADM01C`) and regular users to the main menu (`COMEN01C`).

| Screen / Nav item | Admin (`admin`) | Regular user (`user0001`) |
|-------------------|:---------------:|:-------------------------:|
| Dashboard         | ✓ | ✓ |
| Accounts          | ✓ | ✓ |
| Cards             | ✓ | ✓ |
| Transactions      | ✓ | ✓ |
| Reports           | ✓ | ✓ |
| Billing           | ✓ | ✓ |
| **Users**         | ✓ | ✗ (hidden) |
| **Admin**         | ✓ | ✗ (hidden) |

How it is enforced:

- **Navigation:** the sidebar filters `adminOnly` items, so a regular user never
  sees the **Users** or **Admin** links
  (`frontend/src/components/Sidebar.tsx`).
- **Routing:** the `/admin` and `/users/*` routes are wrapped in an `AdminRoute`
  guard — a regular user who types the URL directly is redirected to `/`
  (`frontend/src/App.tsx`).
- **API:** the user-admin endpoints require an admin token
  (`backend/app/routers/users.py`).

### How to verify

1. Log in as `user0001 / user1234` — the sidebar shows Dashboard, Accounts,
   Cards, Transactions, Reports, Billing only. Visiting `/users` or `/admin`
   redirects back to the dashboard.
2. Log out and log in as `admin / admin123` — the sidebar now also shows
   **Users** and **Admin**, and both screens open.

## Project layout

```
carddemo-modern/
├── backend/    # FastAPI app, SQLAlchemy models, batch services, seed migration
├── frontend/   # React (Vite + TS + MUI) pages mapping 1:1 to BMS maps
├── database/   # schema.sql
├── setup.sh    # first-time local setup
├── run.sh       # start backend + frontend
└── docker-compose.yml
```
