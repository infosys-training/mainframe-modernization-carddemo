"""Pydantic schemas for request/response validation."""

from datetime import date, datetime
from decimal import Decimal

from pydantic import BaseModel, Field


# --- Auth ---
class LoginRequest(BaseModel):
    user_id: str = Field(..., max_length=8)
    password: str = Field(..., max_length=64)


class TokenResponse(BaseModel):
    access_token: str
    token_type: str = "bearer"
    user_type: str
    user_id: str


# --- User ---
class UserBase(BaseModel):
    user_id: str = Field(..., max_length=8)
    first_name: str = Field(default="", max_length=20)
    last_name: str = Field(default="", max_length=20)
    user_type: str = Field(default="U", pattern="^[AU]$")


class UserCreate(UserBase):
    password: str = Field(..., min_length=1, max_length=64)


class UserUpdate(BaseModel):
    first_name: str | None = Field(default=None, max_length=20)
    last_name: str | None = Field(default=None, max_length=20)
    user_type: str | None = Field(default=None, pattern="^[AU]$")
    password: str | None = Field(default=None, min_length=1, max_length=64)


class UserResponse(UserBase):
    created_at: datetime | None = None
    updated_at: datetime | None = None

    class Config:
        from_attributes = True


# --- Customer ---
class CustomerBase(BaseModel):
    first_name: str = Field(default="", max_length=25)
    middle_name: str = Field(default="", max_length=25)
    last_name: str = Field(default="", max_length=25)
    addr_line_1: str = Field(default="", max_length=50)
    addr_line_2: str = Field(default="", max_length=50)
    addr_line_3: str = Field(default="", max_length=50)
    addr_state_cd: str = Field(default="", max_length=2)
    addr_country_cd: str = Field(default="USA", max_length=3)
    addr_zip: str = Field(default="", max_length=10)
    phone_num_1: str = Field(default="", max_length=15)
    phone_num_2: str = Field(default="", max_length=15)
    ssn: str = Field(default="", max_length=9)
    govt_issued_id: str = Field(default="", max_length=20)
    dob: date | None = None
    eft_account_id: str = Field(default="", max_length=10)
    pri_card_holder_ind: str = Field(default="Y", max_length=1)
    fico_credit_score: int = Field(default=0, ge=0, le=850)


class CustomerCreate(CustomerBase):
    pass


class CustomerResponse(CustomerBase):
    cust_id: int
    created_at: datetime | None = None
    updated_at: datetime | None = None

    class Config:
        from_attributes = True


# --- Account ---
class AccountBase(BaseModel):
    active_status: str = Field(default="Y", pattern="^[YN]$")
    curr_bal: Decimal = Field(default=Decimal("0.00"))
    credit_limit: Decimal = Field(default=Decimal("0.00"))
    cash_credit_limit: Decimal = Field(default=Decimal("0.00"))
    open_date: date | None = None
    expiration_date: date | None = None
    reissue_date: date | None = None
    curr_cyc_credit: Decimal = Field(default=Decimal("0.00"))
    curr_cyc_debit: Decimal = Field(default=Decimal("0.00"))
    addr_zip: str = Field(default="", max_length=10)
    group_id: str = Field(default="", max_length=10)


class AccountCreate(AccountBase):
    acct_id: int


class AccountUpdate(BaseModel):
    active_status: str | None = Field(default=None, pattern="^[YN]$")
    curr_bal: Decimal | None = None
    credit_limit: Decimal | None = None
    cash_credit_limit: Decimal | None = None
    open_date: date | None = None
    expiration_date: date | None = None
    reissue_date: date | None = None
    curr_cyc_credit: Decimal | None = None
    curr_cyc_debit: Decimal | None = None
    addr_zip: str | None = Field(default=None, max_length=10)
    group_id: str | None = Field(default=None, max_length=10)


class AccountResponse(AccountBase):
    acct_id: int
    created_at: datetime | None = None
    updated_at: datetime | None = None

    class Config:
        from_attributes = True


# --- Card ---
class CardBase(BaseModel):
    acct_id: int
    cvv_cd: str = Field(default="", max_length=3)
    embossed_name: str = Field(default="", max_length=50)
    expiration_date: date | None = None
    active_status: str = Field(default="Y", pattern="^[YN]$")


class CardCreate(CardBase):
    card_num: str = Field(..., max_length=16)


class CardUpdate(BaseModel):
    acct_id: int | None = None
    cvv_cd: str | None = Field(default=None, max_length=3)
    embossed_name: str | None = Field(default=None, max_length=50)
    expiration_date: date | None = None
    active_status: str | None = Field(default=None, pattern="^[YN]$")


class CardResponse(CardBase):
    card_num: str
    created_at: datetime | None = None
    updated_at: datetime | None = None

    class Config:
        from_attributes = True


# --- Transaction ---
class TransactionBase(BaseModel):
    tran_type_cd: str = Field(default="", max_length=2)
    tran_cat_cd: int = Field(default=0)
    tran_source: str = Field(default="", max_length=10)
    tran_desc: str = Field(default="", max_length=100)
    tran_amt: Decimal = Field(default=Decimal("0.00"))
    merchant_id: int = Field(default=0)
    merchant_name: str = Field(default="", max_length=50)
    merchant_city: str = Field(default="", max_length=50)
    merchant_zip: str = Field(default="", max_length=10)
    card_num: str = Field(default="", max_length=16)


class TransactionCreate(TransactionBase):
    pass


class TransactionResponse(TransactionBase):
    tran_id: str
    orig_ts: datetime | None = None
    proc_ts: datetime | None = None
    created_at: datetime | None = None

    class Config:
        from_attributes = True


# --- Transaction Type ---
class TransactionTypeResponse(BaseModel):
    tran_type_cd: str
    tran_type_desc: str

    class Config:
        from_attributes = True


# --- Transaction Category ---
class TransactionCategoryResponse(BaseModel):
    tran_type_cd: str
    tran_cat_cd: int
    tran_cat_desc: str

    class Config:
        from_attributes = True


# --- Pagination ---
class PaginatedResponse(BaseModel):
    items: list
    total: int
    page: int
    page_size: int
    total_pages: int


# --- Reports ---
class TransactionReportRequest(BaseModel):
    start_date: date | None = None
    end_date: date | None = None
    acct_id: int | None = None


class TransactionReportItem(BaseModel):
    tran_id: str
    acct_id: int | None = None
    tran_type_cd: str
    tran_type_desc: str = ""
    tran_cat_cd: int
    tran_cat_desc: str = ""
    tran_source: str
    tran_amt: Decimal
    orig_ts: datetime | None = None


class TransactionReportResponse(BaseModel):
    report_name: str = "Daily Transaction Report"
    start_date: date | None = None
    end_date: date | None = None
    items: list[TransactionReportItem]
    total_amount: Decimal
    record_count: int


# --- Billing ---
class BillingStatementResponse(BaseModel):
    acct_id: int
    customer_name: str
    statement_date: date
    current_balance: Decimal
    credit_limit: Decimal
    available_credit: Decimal
    cycle_credits: Decimal
    cycle_debits: Decimal
    transactions: list[TransactionResponse]
