"""Reports router replacing CORPT00C.cbl (transaction reports)."""

from datetime import date, datetime, timedelta
from decimal import Decimal

from fastapi import APIRouter, Depends, Query
from sqlalchemy.orm import Session

from app.database import get_db
from app.models.models import (
    CardXref,
    Transaction,
    TransactionCategory,
    TransactionType,
)
from app.schemas.schemas import TransactionReportItem, TransactionReportResponse
from app.services.auth import get_current_user

router = APIRouter(prefix="/api/reports", tags=["reports"])


@router.get("/transactions", response_model=TransactionReportResponse)
def transaction_report(
    start_date: date | None = Query(None),
    end_date: date | None = Query(None),
    acct_id: int | None = Query(None),
    db: Session = Depends(get_db),
    _=Depends(get_current_user),
):
    if not start_date:
        start_date = date.today() - timedelta(days=30)
    if not end_date:
        end_date = date.today()

    start_dt = datetime(start_date.year, start_date.month, start_date.day)
    end_dt = datetime(end_date.year, end_date.month, end_date.day, 23, 59, 59)

    query = db.query(Transaction).filter(
        Transaction.orig_ts >= start_dt,
        Transaction.orig_ts <= end_dt,
    )

    if acct_id:
        card_nums = (
            db.query(CardXref.xref_card_num)
            .filter(CardXref.xref_acct_id == acct_id)
            .subquery()
        )
        query = query.filter(Transaction.card_num.in_(card_nums))

    transactions = query.order_by(Transaction.orig_ts).all()

    type_map: dict[str, str] = {}
    for tt in db.query(TransactionType).all():
        type_map[tt.tran_type_cd] = tt.tran_type_desc

    cat_map: dict[tuple[str, int], str] = {}
    for tc in db.query(TransactionCategory).all():
        cat_map[(tc.tran_type_cd, tc.tran_cat_cd)] = tc.tran_cat_desc

    xref_map: dict[str, int] = {}
    for xref in db.query(CardXref).all():
        xref_map[xref.xref_card_num] = xref.xref_acct_id

    items = []
    total_amount = Decimal("0.00")
    for txn in transactions:
        item = TransactionReportItem(
            tran_id=txn.tran_id,
            acct_id=xref_map.get(txn.card_num),
            tran_type_cd=txn.tran_type_cd,
            tran_type_desc=type_map.get(txn.tran_type_cd, ""),
            tran_cat_cd=txn.tran_cat_cd,
            tran_cat_desc=cat_map.get((txn.tran_type_cd, txn.tran_cat_cd), ""),
            tran_source=txn.tran_source,
            tran_amt=txn.tran_amt,
            orig_ts=txn.orig_ts,
        )
        items.append(item)
        total_amount += Decimal(str(txn.tran_amt))

    return TransactionReportResponse(
        start_date=start_date,
        end_date=end_date,
        items=items,
        total_amount=total_amount,
        record_count=len(items),
    )
