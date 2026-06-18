"""Card router replacing COCRDLIC.cbl (list), COCRDSLC.cbl (select), COCRDUPC.cbl (update)."""

from fastapi import APIRouter, Depends, HTTPException, Query, status
from sqlalchemy.orm import Session

from app.database import get_db
from app.models.models import Card
from app.schemas.schemas import CardResponse, CardUpdate
from app.services.auth import get_current_user

router = APIRouter(prefix="/api/cards", tags=["cards"])


@router.get("", response_model=list[CardResponse])
def list_cards(
    page: int = Query(1, ge=1),
    page_size: int = Query(20, ge=1, le=100),
    acct_id: int | None = Query(None),
    db: Session = Depends(get_db),
    _=Depends(get_current_user),
):
    query = db.query(Card)
    if acct_id is not None:
        query = query.filter(Card.acct_id == acct_id)
    query = query.order_by(Card.card_num)
    offset = (page - 1) * page_size
    cards = query.offset(offset).limit(page_size).all()
    return cards


@router.get("/{card_num}", response_model=CardResponse)
def get_card(
    card_num: str,
    db: Session = Depends(get_db),
    _=Depends(get_current_user),
):
    card = db.query(Card).filter(Card.card_num == card_num).first()
    if not card:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Card not found")
    return card


@router.put("/{card_num}", response_model=CardResponse)
def update_card(
    card_num: str,
    data: CardUpdate,
    db: Session = Depends(get_db),
    _=Depends(get_current_user),
):
    card = db.query(Card).filter(Card.card_num == card_num).first()
    if not card:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Card not found")
    update_data = data.model_dump(exclude_unset=True)
    for field, value in update_data.items():
        setattr(card, field, value)
    db.commit()
    db.refresh(card)
    return card
