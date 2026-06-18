"""Auth router replacing COSGN00C.cbl (sign-on/sign-off)."""

from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session

from app.database import get_db
from app.models.models import User
from app.schemas.schemas import LoginRequest, TokenResponse
from app.services.auth import (
    create_access_token,
    get_current_user,
    verify_password,
)

router = APIRouter(prefix="/api/auth", tags=["auth"])


@router.post("/login", response_model=TokenResponse)
def login(request: LoginRequest, db: Session = Depends(get_db)):
    user = db.query(User).filter(User.user_id == request.user_id).first()
    if not user or not verify_password(request.password, user.password_hash):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid user ID or password",
        )
    token = create_access_token(
        data={"sub": user.user_id, "user_type": user.user_type}
    )
    return TokenResponse(
        access_token=token,
        user_type=user.user_type,
        user_id=user.user_id,
    )


@router.post("/logout")
def logout(current_user: User = Depends(get_current_user)):
    return {"message": "Logged out successfully"}
