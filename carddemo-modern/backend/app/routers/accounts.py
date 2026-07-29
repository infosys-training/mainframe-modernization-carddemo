"""Account router replacing COACTVWC.cbl (view) and COACTUPC.cbl (update)."""

from fastapi import APIRouter, Depends, HTTPException, Query, status
from sqlalchemy.orm import Session

from app.database import get_db
from app.models.models import Account
from app.schemas.schemas import AccountCreate, AccountResponse, AccountUpdate
from app.services.auth import get_current_user

router = APIRouter(prefix="/api/accounts", tags=["accounts"])


@router.post("", response_model=AccountResponse, status_code=status.HTTP_201_CREATED)
def create_account(
    data: AccountCreate,
    db: Session = Depends(get_db),
    _=Depends(get_current_user),
):
    existing = db.query(Account).filter(Account.acct_id == data.acct_id).first()
    if existing:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail=f"Account {data.acct_id} already exists",
        )
    account = Account(**data.model_dump())
    db.add(account)
    db.commit()
    db.refresh(account)
    return account


@router.get("", response_model=list[AccountResponse])
def list_accounts(
    page: int = Query(1, ge=1),
    page_size: int = Query(20, ge=1, le=100),
    active_only: bool = Query(False),
    db: Session = Depends(get_db),
    _=Depends(get_current_user),
):
    query = db.query(Account)
    if active_only:
        query = query.filter(Account.active_status == "Y")
    query = query.order_by(Account.acct_id)
    offset = (page - 1) * page_size
    accounts = query.offset(offset).limit(page_size).all()
    return accounts


@router.get("/{acct_id}", response_model=AccountResponse)
def get_account(
    acct_id: int,
    db: Session = Depends(get_db),
    _=Depends(get_current_user),
):
    account = db.query(Account).filter(Account.acct_id == acct_id).first()
    if not account:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Account not found")
    return account


@router.put("/{acct_id}", response_model=AccountResponse)
def update_account(
    acct_id: int,
    data: AccountUpdate,
    db: Session = Depends(get_db),
    _=Depends(get_current_user),
):
    account = db.query(Account).filter(Account.acct_id == acct_id).first()
    if not account:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Account not found")
    update_data = data.model_dump(exclude_unset=True)
    for field, value in update_data.items():
        setattr(account, field, value)
    db.commit()
    db.refresh(account)
    return account
