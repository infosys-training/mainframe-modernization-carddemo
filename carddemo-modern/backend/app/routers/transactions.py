"""Transaction router replacing COTRN00C.cbl (list), COTRN01C.cbl (view), COTRN02C.cbl (add)."""

import uuid
from datetime import datetime

from fastapi import APIRouter, Depends, HTTPException, Query, status
from sqlalchemy.orm import Session

from app.database import get_db
from app.models.models import Transaction
from app.schemas.schemas import TransactionCreate, TransactionResponse
from app.services.auth import get_current_user

router = APIRouter(prefix="/api/transactions", tags=["transactions"])


@router.get("", response_model=list[TransactionResponse])
def list_transactions(
    page: int = Query(1, ge=1),
    page_size: int = Query(20, ge=1, le=100),
    card_num: str | None = Query(None),
    acct_id: int | None = Query(None),
    db: Session = Depends(get_db),
    _=Depends(get_current_user),
):
    query = db.query(Transaction)
    if card_num:
        query = query.filter(Transaction.card_num == card_num)
    if acct_id:
        from app.models.models import CardXref

        card_nums = (
            db.query(CardXref.xref_card_num)
            .filter(CardXref.xref_acct_id == acct_id)
            .subquery()
        )
        query = query.filter(Transaction.card_num.in_(card_nums))
    query = query.order_by(Transaction.orig_ts.desc())
    offset = (page - 1) * page_size
    transactions = query.offset(offset).limit(page_size).all()
    return transactions


@router.get("/{tran_id}", response_model=TransactionResponse)
def get_transaction(
    tran_id: str,
    db: Session = Depends(get_db),
    _=Depends(get_current_user),
):
    txn = db.query(Transaction).filter(Transaction.tran_id == tran_id).first()
    if not txn:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail="Transaction not found"
        )
    return txn


@router.post("", response_model=TransactionResponse, status_code=status.HTTP_201_CREATED)
def add_transaction(
    data: TransactionCreate,
    db: Session = Depends(get_db),
    _=Depends(get_current_user),
):
    tran_id = uuid.uuid4().hex[:16].upper()
    now = datetime.utcnow()
    txn = Transaction(
        tran_id=tran_id,
        tran_type_cd=data.tran_type_cd,
        tran_cat_cd=data.tran_cat_cd,
        tran_source=data.tran_source,
        tran_desc=data.tran_desc,
        tran_amt=data.tran_amt,
        merchant_id=data.merchant_id,
        merchant_name=data.merchant_name,
        merchant_city=data.merchant_city,
        merchant_zip=data.merchant_zip,
        card_num=data.card_num,
        orig_ts=now,
        proc_ts=None,
    )
    db.add(txn)
    db.commit()
    db.refresh(txn)
    return txn
