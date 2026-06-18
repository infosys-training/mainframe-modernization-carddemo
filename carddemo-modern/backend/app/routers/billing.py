"""Billing router replacing COBIL00C.cbl (bill payment/statements)."""

from datetime import date, datetime
from decimal import Decimal

from fastapi import APIRouter, Depends, HTTPException, Query, status
from sqlalchemy.orm import Session

from app.database import get_db
from app.models.models import Account, CardXref, Customer, Transaction
from app.schemas.schemas import BillingStatementResponse, TransactionResponse
from app.services.auth import get_current_user

router = APIRouter(prefix="/api/billing", tags=["billing"])


@router.get("/statements", response_model=list[BillingStatementResponse])
def list_statements(
    acct_id: int | None = Query(None),
    db: Session = Depends(get_db),
    _=Depends(get_current_user),
):
    query = db.query(Account)
    if acct_id:
        query = query.filter(Account.acct_id == acct_id)
    accounts = query.order_by(Account.acct_id).limit(50).all()

    statements = []
    for account in accounts:
        xref = (
            db.query(CardXref).filter(CardXref.xref_acct_id == account.acct_id).first()
        )
        customer_name = ""
        if xref:
            customer = (
                db.query(Customer)
                .filter(Customer.cust_id == xref.xref_cust_id)
                .first()
            )
            if customer:
                customer_name = (
                    f"{customer.first_name} {customer.last_name}".strip()
                )

        card_nums = [
            x.xref_card_num
            for x in db.query(CardXref)
            .filter(CardXref.xref_acct_id == account.acct_id)
            .all()
        ]

        recent_txns = (
            db.query(Transaction)
            .filter(Transaction.card_num.in_(card_nums))
            .order_by(Transaction.orig_ts.desc())
            .limit(50)
            .all()
        )

        credit_limit = Decimal(str(account.credit_limit))
        curr_bal = Decimal(str(account.curr_bal))

        stmt = BillingStatementResponse(
            acct_id=account.acct_id,
            customer_name=customer_name,
            statement_date=date.today(),
            current_balance=curr_bal,
            credit_limit=credit_limit,
            available_credit=credit_limit - curr_bal,
            cycle_credits=Decimal(str(account.curr_cyc_credit)),
            cycle_debits=Decimal(str(account.curr_cyc_debit)),
            transactions=[TransactionResponse.model_validate(t) for t in recent_txns],
        )
        statements.append(stmt)

    return statements
