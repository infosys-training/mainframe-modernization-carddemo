"""SQLAlchemy models mapped from COBOL copybooks."""

from datetime import date, datetime

from sqlalchemy import (
    BigInteger,
    CheckConstraint,
    Date,
    DateTime,
    ForeignKey,
    Integer,
    Numeric,
    String,
)
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.database import Base


class User(Base):
    """Maps to CSUSR01Y.cpy SEC-USER-DATA (80-byte VSAM record)."""

    __tablename__ = "users"

    user_id: Mapped[str] = mapped_column(String(8), primary_key=True)
    first_name: Mapped[str] = mapped_column(String(20), default="")
    last_name: Mapped[str] = mapped_column(String(20), default="")
    password_hash: Mapped[str] = mapped_column(String(128))
    user_type: Mapped[str] = mapped_column(
        String(1),
        default="U",
        info={"description": "A=Admin, U=Regular user (was SEC-USR-TYPE)"},
    )
    created_at: Mapped[datetime] = mapped_column(DateTime, default=datetime.utcnow)
    updated_at: Mapped[datetime] = mapped_column(
        DateTime, default=datetime.utcnow, onupdate=datetime.utcnow
    )

    __table_args__ = (CheckConstraint("user_type IN ('A', 'U')"),)


class Customer(Base):
    """Maps to CVCUS01Y.cpy CUSTOMER-RECORD (500-byte VSAM record)."""

    __tablename__ = "customers"

    cust_id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    first_name: Mapped[str] = mapped_column(String(25), default="")
    middle_name: Mapped[str] = mapped_column(String(25), default="")
    last_name: Mapped[str] = mapped_column(String(25), default="")
    addr_line_1: Mapped[str] = mapped_column(String(50), default="")
    addr_line_2: Mapped[str] = mapped_column(String(50), default="")
    addr_line_3: Mapped[str] = mapped_column(String(50), default="")
    addr_state_cd: Mapped[str] = mapped_column(String(2), default="")
    addr_country_cd: Mapped[str] = mapped_column(String(3), default="USA")
    addr_zip: Mapped[str] = mapped_column(String(10), default="")
    phone_num_1: Mapped[str] = mapped_column(String(15), default="")
    phone_num_2: Mapped[str] = mapped_column(String(15), default="")
    ssn: Mapped[str] = mapped_column(String(9), default="")
    govt_issued_id: Mapped[str] = mapped_column(String(20), default="")
    dob: Mapped[date | None] = mapped_column(Date, nullable=True)
    eft_account_id: Mapped[str] = mapped_column(String(10), default="")
    pri_card_holder_ind: Mapped[str] = mapped_column(String(1), default="Y")
    fico_credit_score: Mapped[int] = mapped_column(Integer, default=0)
    created_at: Mapped[datetime] = mapped_column(DateTime, default=datetime.utcnow)
    updated_at: Mapped[datetime] = mapped_column(
        DateTime, default=datetime.utcnow, onupdate=datetime.utcnow
    )

    xrefs: Mapped[list["CardXref"]] = relationship(back_populates="customer")


class Account(Base):
    """Maps to CVACT01Y.cpy ACCOUNT-RECORD (300-byte VSAM record)."""

    __tablename__ = "accounts"

    acct_id: Mapped[int] = mapped_column(BigInteger, primary_key=True)
    active_status: Mapped[str] = mapped_column(String(1), default="Y")
    curr_bal: Mapped[float] = mapped_column(Numeric(12, 2), default=0.00)
    credit_limit: Mapped[float] = mapped_column(Numeric(12, 2), default=0.00)
    cash_credit_limit: Mapped[float] = mapped_column(Numeric(12, 2), default=0.00)
    open_date: Mapped[date | None] = mapped_column(Date, nullable=True)
    expiration_date: Mapped[date | None] = mapped_column(Date, nullable=True)
    reissue_date: Mapped[date | None] = mapped_column(Date, nullable=True)
    curr_cyc_credit: Mapped[float] = mapped_column(Numeric(12, 2), default=0.00)
    curr_cyc_debit: Mapped[float] = mapped_column(Numeric(12, 2), default=0.00)
    addr_zip: Mapped[str] = mapped_column(String(10), default="")
    group_id: Mapped[str] = mapped_column(String(10), default="")
    created_at: Mapped[datetime] = mapped_column(DateTime, default=datetime.utcnow)
    updated_at: Mapped[datetime] = mapped_column(
        DateTime, default=datetime.utcnow, onupdate=datetime.utcnow
    )

    cards: Mapped[list["Card"]] = relationship(back_populates="account")
    xrefs: Mapped[list["CardXref"]] = relationship(back_populates="account")

    __table_args__ = (CheckConstraint("active_status IN ('Y', 'N')"),)


class Card(Base):
    """Maps to CVACT02Y.cpy CARD-RECORD (150-byte VSAM record)."""

    __tablename__ = "cards"

    card_num: Mapped[str] = mapped_column(String(16), primary_key=True)
    acct_id: Mapped[int] = mapped_column(
        BigInteger, ForeignKey("accounts.acct_id"), nullable=False
    )
    cvv_cd: Mapped[str] = mapped_column(String(3), default="")
    embossed_name: Mapped[str] = mapped_column(String(50), default="")
    expiration_date: Mapped[date | None] = mapped_column(Date, nullable=True)
    active_status: Mapped[str] = mapped_column(String(1), default="Y")
    created_at: Mapped[datetime] = mapped_column(DateTime, default=datetime.utcnow)
    updated_at: Mapped[datetime] = mapped_column(
        DateTime, default=datetime.utcnow, onupdate=datetime.utcnow
    )

    account: Mapped["Account"] = relationship(back_populates="cards")

    __table_args__ = (CheckConstraint("active_status IN ('Y', 'N')"),)


class CardXref(Base):
    """Maps to CVACT03Y.cpy CARD-XREF-RECORD (50-byte VSAM record)."""

    __tablename__ = "card_xref"

    xref_card_num: Mapped[str] = mapped_column(String(16), primary_key=True)
    xref_cust_id: Mapped[int] = mapped_column(
        Integer, ForeignKey("customers.cust_id"), nullable=False
    )
    xref_acct_id: Mapped[int] = mapped_column(
        BigInteger, ForeignKey("accounts.acct_id"), nullable=False
    )

    customer: Mapped["Customer"] = relationship(back_populates="xrefs")
    account: Mapped["Account"] = relationship(back_populates="xrefs")


class TransactionType(Base):
    """Maps to CVTRA03Y.cpy TRAN-TYPE-RECORD (60-byte VSAM record)."""

    __tablename__ = "transaction_types"

    tran_type_cd: Mapped[str] = mapped_column(String(2), primary_key=True)
    tran_type_desc: Mapped[str] = mapped_column(String(50), default="")


class TransactionCategory(Base):
    """Maps to CVTRA04Y.cpy TRAN-CAT-RECORD (60-byte VSAM record)."""

    __tablename__ = "transaction_categories"

    tran_type_cd: Mapped[str] = mapped_column(
        String(2), ForeignKey("transaction_types.tran_type_cd"), primary_key=True
    )
    tran_cat_cd: Mapped[int] = mapped_column(Integer, primary_key=True)
    tran_cat_desc: Mapped[str] = mapped_column(String(50), default="")


class Transaction(Base):
    """Maps to CVTRA05Y.cpy TRAN-RECORD (350-byte VSAM record)."""

    __tablename__ = "transactions"

    tran_id: Mapped[str] = mapped_column(String(16), primary_key=True)
    tran_type_cd: Mapped[str] = mapped_column(String(2), default="")
    tran_cat_cd: Mapped[int] = mapped_column(Integer, default=0)
    tran_source: Mapped[str] = mapped_column(String(10), default="")
    tran_desc: Mapped[str] = mapped_column(String(100), default="")
    tran_amt: Mapped[float] = mapped_column(Numeric(11, 2), default=0.00)
    merchant_id: Mapped[int] = mapped_column(BigInteger, default=0)
    merchant_name: Mapped[str] = mapped_column(String(50), default="")
    merchant_city: Mapped[str] = mapped_column(String(50), default="")
    merchant_zip: Mapped[str] = mapped_column(String(10), default="")
    card_num: Mapped[str] = mapped_column(String(16), default="")
    orig_ts: Mapped[datetime | None] = mapped_column(DateTime, nullable=True)
    proc_ts: Mapped[datetime | None] = mapped_column(DateTime, nullable=True)
    created_at: Mapped[datetime] = mapped_column(DateTime, default=datetime.utcnow)


class TranCatBal(Base):
    """Maps to CVTRA01Y.cpy TRAN-CAT-BAL-RECORD (50-byte VSAM record)."""

    __tablename__ = "tran_cat_bal"

    acct_id: Mapped[int] = mapped_column(
        BigInteger, ForeignKey("accounts.acct_id"), primary_key=True
    )
    tran_type_cd: Mapped[str] = mapped_column(String(2), primary_key=True)
    tran_cat_cd: Mapped[int] = mapped_column(Integer, primary_key=True)
    cat_bal: Mapped[float] = mapped_column(Numeric(11, 2), default=0.00)


class DisclosureGroup(Base):
    """Maps to CVTRA02Y.cpy DIS-GROUP-RECORD (50-byte VSAM record)."""

    __tablename__ = "disclosure_groups"

    acct_group_id: Mapped[str] = mapped_column(String(10), primary_key=True)
    tran_type_cd: Mapped[str] = mapped_column(String(2), primary_key=True)
    tran_cat_cd: Mapped[int] = mapped_column(Integer, primary_key=True)
    interest_rate: Mapped[float] = mapped_column(Numeric(6, 2), default=0.00)
