"""FastAPI application entry point — replaces CICS transaction routing."""

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.routers import accounts, auth, billing, cards, reports, transactions, users

app = FastAPI(
    title="CardDemo Modern",
    description="Modernized credit card management system (migrated from COBOL/CICS/VSAM)",
    version="1.0.0",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:5173", "http://localhost:3000"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
    expose_headers=["X-Total-Count"],
)

app.include_router(auth.router)
app.include_router(accounts.router)
app.include_router(cards.router)
app.include_router(transactions.router)
app.include_router(users.router)
app.include_router(reports.router)
app.include_router(billing.router)


@app.get("/api/health")
def health_check():
    return {"status": "ok", "application": "CardDemo Modern"}
