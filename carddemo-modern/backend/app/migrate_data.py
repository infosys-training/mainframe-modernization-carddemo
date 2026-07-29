"""Data migration script: loads seed data from app/data/ASCII/ into PostgreSQL.

Handles COBOL overpunch sign encoding for monetary fields:
  Positive: { = +0, A-I = +1..+9
  Negative: } = -0, J-R = -1..-9
"""

import os
import sys
from datetime import date, datetime
from decimal import Decimal
from pathlib import Path

from sqlalchemy.orm import Session

# Ensure the parent directory is on the path
sys.path.insert(0, str(Path(__file__).resolve().parent.parent))

from app.database import SessionLocal, engine
from app.models.models import (
    Account,
    Base,
    Card,
    CardXref,
    Customer,
    DisclosureGroup,
    TranCatBal,
    Transaction,
    TransactionCategory,
    TransactionType,
    User,
)
from app.services.auth import hash_password

DATA_DIR = Path(os.environ.get(
    "CARDDEMO_DATA_DIR",
    str(Path(__file__).resolve().parent.parent.parent.parent / "app" / "data" / "ASCII"),
))

# COBOL overpunch decoding
OVERPUNCH_POS = {"{": 0, "A": 1, "B": 2, "C": 3, "D": 4, "E": 5, "F": 6, "G": 7, "H": 8, "I": 9}
OVERPUNCH_NEG = {"}": 0, "J": 1, "K": 2, "L": 3, "M": 4, "N": 5, "O": 6, "P": 7, "Q": 8, "R": 9}


def decode_signed_decimal(raw: str, scale: int = 2) -> Decimal:
    """Decode COBOL PIC S9(n)V9(m) with trailing overpunch sign."""
    raw = raw.strip()
    if not raw:
        return Decimal("0.00")
    last_char = raw[-1]
    digits = raw[:-1]
    if last_char in OVERPUNCH_POS:
        sign = 1
        last_digit = OVERPUNCH_POS[last_char]
    elif last_char in OVERPUNCH_NEG:
        sign = -1
        last_digit = OVERPUNCH_NEG[last_char]
    elif last_char.isdigit():
        return Decimal(raw) / Decimal(10**scale)
    else:
        return Decimal("0.00")
    full_digits = digits + str(last_digit)
    value = Decimal(full_digits) * sign
    return value / Decimal(10**scale)


def parse_date(s: str) -> date | None:
    s = s.strip()
    if not s or s == "0000-00-00":
        return None
    try:
        return datetime.strptime(s, "%Y-%m-%d").date()
    except ValueError:
        return None


def parse_timestamp(s: str) -> datetime | None:
    s = s.strip()
    if not s:
        return None
    try:
        return datetime.strptime(s[:19], "%Y-%m-%d %H:%M:%S")
    except ValueError:
        return None


def load_accounts(db: Session) -> None:
    """Parse acctdata.txt per CVACT01Y.cpy layout (300-byte record)."""
    filepath = DATA_DIR / "acctdata.txt"
    if not filepath.exists():
        print(f"  Skipping accounts: {filepath} not found")
        return
    content = filepath.read_text()
    lines = [l for l in content.split("\n") if l.strip()]
    count = 0
    for line in lines:
        if len(line) < 100:
            continue
        acct_id = int(line[0:11])
        active_status = line[11:12]
        curr_bal = decode_signed_decimal(line[12:24], 2)
        credit_limit = decode_signed_decimal(line[24:36], 2)
        cash_credit_limit = decode_signed_decimal(line[36:48], 2)
        open_date = parse_date(line[48:58])
        expiration_date = parse_date(line[58:68])
        reissue_date = parse_date(line[68:78])
        curr_cyc_credit = decode_signed_decimal(line[78:90], 2)
        curr_cyc_debit = decode_signed_decimal(line[90:102], 2)
        addr_zip = line[102:112].strip()
        group_id = line[112:122].strip()

        existing = db.query(Account).filter(Account.acct_id == acct_id).first()
        if existing:
            continue

        account = Account(
            acct_id=acct_id,
            active_status=active_status,
            curr_bal=curr_bal,
            credit_limit=credit_limit,
            cash_credit_limit=cash_credit_limit,
            open_date=open_date,
            expiration_date=expiration_date,
            reissue_date=reissue_date,
            curr_cyc_credit=curr_cyc_credit,
            curr_cyc_debit=curr_cyc_debit,
            addr_zip=addr_zip,
            group_id=group_id,
        )
        db.add(account)
        count += 1
    db.commit()
    print(f"  Loaded {count} accounts")


def load_cards(db: Session) -> None:
    """Parse carddata.txt per CVACT02Y.cpy layout (150-byte record)."""
    filepath = DATA_DIR / "carddata.txt"
    if not filepath.exists():
        print(f"  Skipping cards: {filepath} not found")
        return
    content = filepath.read_text()
    lines = [l for l in content.split("\n") if l.strip()]
    count = 0
    for line in lines:
        if len(line) < 80:
            continue
        card_num = line[0:16].strip()
        acct_id = int(line[16:27])
        cvv_cd = line[27:30]
        embossed_name = line[30:80].strip()
        expiration_date_str = line[80:90].strip() if len(line) >= 90 else ""
        active_status = line[90:91] if len(line) >= 91 else "Y"

        existing = db.query(Card).filter(Card.card_num == card_num).first()
        if existing:
            continue

        card = Card(
            card_num=card_num,
            acct_id=acct_id,
            cvv_cd=cvv_cd,
            embossed_name=embossed_name,
            expiration_date=parse_date(expiration_date_str),
            active_status=active_status,
        )
        db.add(card)
        count += 1
    db.commit()
    print(f"  Loaded {count} cards")


def load_customers(db: Session) -> None:
    """Parse custdata.txt per CVCUS01Y.cpy layout (500-byte record)."""
    filepath = DATA_DIR / "custdata.txt"
    if not filepath.exists():
        print(f"  Skipping customers: {filepath} not found")
        return
    content = filepath.read_text()
    lines = [l for l in content.split("\n") if l.strip()]
    count = 0
    for line in lines:
        if len(line) < 200:
            continue
        cust_id = int(line[0:9])
        first_name = line[9:34].strip()
        middle_name = line[34:59].strip()
        last_name = line[59:84].strip()
        addr_line_1 = line[84:134].strip()
        addr_line_2 = line[134:184].strip()
        addr_line_3 = line[184:234].strip()
        addr_state_cd = line[234:236].strip()
        addr_country_cd = line[236:239].strip()
        addr_zip = line[239:249].strip()
        phone_num_1 = line[249:264].strip()
        phone_num_2 = line[264:279].strip()
        ssn = line[279:288].strip()
        govt_issued_id = line[288:308].strip()
        dob_str = line[308:318].strip() if len(line) >= 318 else ""
        eft_account_id = line[318:328].strip() if len(line) >= 328 else ""
        pri_ind = line[328:329] if len(line) >= 329 else "Y"
        fico_str = line[329:332] if len(line) >= 332 else "0"

        existing = db.query(Customer).filter(Customer.cust_id == cust_id).first()
        if existing:
            continue

        customer = Customer(
            cust_id=cust_id,
            first_name=first_name,
            middle_name=middle_name,
            last_name=last_name,
            addr_line_1=addr_line_1,
            addr_line_2=addr_line_2,
            addr_line_3=addr_line_3,
            addr_state_cd=addr_state_cd,
            addr_country_cd=addr_country_cd,
            addr_zip=addr_zip,
            phone_num_1=phone_num_1,
            phone_num_2=phone_num_2,
            ssn=ssn,
            govt_issued_id=govt_issued_id,
            dob=parse_date(dob_str),
            eft_account_id=eft_account_id,
            pri_card_holder_ind=pri_ind,
            fico_credit_score=int(fico_str) if fico_str.isdigit() else 0,
        )
        db.add(customer)
        count += 1
    db.commit()
    print(f"  Loaded {count} customers")


def load_card_xref(db: Session) -> None:
    """Parse cardxref.txt per CVACT03Y.cpy layout (50-byte record)."""
    filepath = DATA_DIR / "cardxref.txt"
    if not filepath.exists():
        print(f"  Skipping card_xref: {filepath} not found")
        return
    content = filepath.read_text()
    lines = [l for l in content.split("\n") if l.strip()]
    count = 0
    for line in lines:
        if len(line) < 36:
            continue
        xref_card_num = line[0:16].strip()
        xref_cust_id = int(line[16:25])
        xref_acct_id = int(line[25:36])

        existing = db.query(CardXref).filter(CardXref.xref_card_num == xref_card_num).first()
        if existing:
            continue

        xref = CardXref(
            xref_card_num=xref_card_num,
            xref_cust_id=xref_cust_id,
            xref_acct_id=xref_acct_id,
        )
        db.add(xref)
        count += 1
    db.commit()
    print(f"  Loaded {count} card xrefs")


def load_transaction_types(db: Session) -> None:
    """Parse trantype.txt per CVTRA03Y.cpy layout (60-byte record)."""
    filepath = DATA_DIR / "trantype.txt"
    if not filepath.exists():
        print(f"  Skipping transaction_types: {filepath} not found")
        return
    content = filepath.read_text()
    lines = [l for l in content.split("\n") if l.strip()]
    count = 0
    for line in lines:
        if len(line) < 4:
            continue
        tran_type_cd = line[0:2]
        tran_type_desc = line[2:52].strip()

        existing = (
            db.query(TransactionType)
            .filter(TransactionType.tran_type_cd == tran_type_cd)
            .first()
        )
        if existing:
            continue

        tt = TransactionType(tran_type_cd=tran_type_cd, tran_type_desc=tran_type_desc)
        db.add(tt)
        count += 1
    db.commit()
    print(f"  Loaded {count} transaction types")


def load_transaction_categories(db: Session) -> None:
    """Parse trancatg.txt per CVTRA04Y.cpy layout (60-byte record)."""
    filepath = DATA_DIR / "trancatg.txt"
    if not filepath.exists():
        print(f"  Skipping transaction_categories: {filepath} not found")
        return
    content = filepath.read_text()
    lines = [l for l in content.split("\n") if l.strip()]
    count = 0
    for line in lines:
        if len(line) < 8:
            continue
        tran_type_cd = line[0:2]
        tran_cat_cd = int(line[2:6])
        tran_cat_desc = line[6:56].strip()

        existing = (
            db.query(TransactionCategory)
            .filter(
                TransactionCategory.tran_type_cd == tran_type_cd,
                TransactionCategory.tran_cat_cd == tran_cat_cd,
            )
            .first()
        )
        if existing:
            continue

        tc = TransactionCategory(
            tran_type_cd=tran_type_cd,
            tran_cat_cd=tran_cat_cd,
            tran_cat_desc=tran_cat_desc,
        )
        db.add(tc)
        count += 1
    db.commit()
    print(f"  Loaded {count} transaction categories")


def load_disclosure_groups(db: Session) -> None:
    """Parse discgrp.txt per CVTRA02Y.cpy layout (50-byte record)."""
    filepath = DATA_DIR / "discgrp.txt"
    if not filepath.exists():
        print(f"  Skipping disclosure_groups: {filepath} not found")
        return
    content = filepath.read_text()
    lines = [l for l in content.split("\n") if l.strip()]
    count = 0
    for line in lines:
        if len(line) < 22:
            continue
        acct_group_id = line[0:10].strip()
        tran_type_cd = line[10:12]
        tran_cat_cd = int(line[12:16])
        interest_rate = decode_signed_decimal(line[16:22], 2)

        existing = (
            db.query(DisclosureGroup)
            .filter(
                DisclosureGroup.acct_group_id == acct_group_id,
                DisclosureGroup.tran_type_cd == tran_type_cd,
                DisclosureGroup.tran_cat_cd == tran_cat_cd,
            )
            .first()
        )
        if existing:
            continue

        dg = DisclosureGroup(
            acct_group_id=acct_group_id,
            tran_type_cd=tran_type_cd,
            tran_cat_cd=tran_cat_cd,
            interest_rate=interest_rate,
        )
        db.add(dg)
        count += 1
    db.commit()
    print(f"  Loaded {count} disclosure groups")


def load_tran_cat_bal(db: Session) -> None:
    """Parse tcatbal.txt per CVTRA01Y.cpy layout (50-byte record)."""
    filepath = DATA_DIR / "tcatbal.txt"
    if not filepath.exists():
        print(f"  Skipping tran_cat_bal: {filepath} not found")
        return
    content = filepath.read_text()
    lines = [l for l in content.split("\n") if l.strip()]
    count = 0
    for line in lines:
        if len(line) < 28:
            continue
        acct_id = int(line[0:11])
        tran_type_cd = line[11:13]
        tran_cat_cd = int(line[13:17])
        cat_bal = decode_signed_decimal(line[17:28], 2)

        existing = (
            db.query(TranCatBal)
            .filter(
                TranCatBal.acct_id == acct_id,
                TranCatBal.tran_type_cd == tran_type_cd,
                TranCatBal.tran_cat_cd == tran_cat_cd,
            )
            .first()
        )
        if existing:
            continue

        tcb = TranCatBal(
            acct_id=acct_id,
            tran_type_cd=tran_type_cd,
            tran_cat_cd=tran_cat_cd,
            cat_bal=cat_bal,
        )
        db.add(tcb)
        count += 1
    db.commit()
    print(f"  Loaded {count} tran cat balances")


def load_transactions(db: Session) -> None:
    """Parse dailytran.txt per CVTRA05Y/CVTRA06Y.cpy layout (350-byte record)."""
    filepath = DATA_DIR / "dailytran.txt"
    if not filepath.exists():
        print(f"  Skipping transactions: {filepath} not found")
        return
    content = filepath.read_text()
    lines = [l for l in content.split("\n") if l.strip()]
    count = 0
    for line in lines:
        if len(line) < 200:
            continue
        tran_id = line[0:16].strip()
        tran_type_cd = line[16:18]
        tran_cat_cd_str = line[18:22]
        tran_cat_cd = int(tran_cat_cd_str) if tran_cat_cd_str.strip().isdigit() else 0
        tran_source = line[22:32].strip()
        tran_desc = line[32:132].strip()
        tran_amt = decode_signed_decimal(line[132:143], 2)
        merchant_id_str = line[143:152]
        merchant_id = int(merchant_id_str) if merchant_id_str.strip().isdigit() else 0
        merchant_name = line[152:202].strip()
        merchant_city = line[202:252].strip()
        merchant_zip = line[252:262].strip()
        card_num = line[262:278].strip()
        orig_ts_str = line[278:304].strip() if len(line) >= 304 else ""
        proc_ts_str = line[304:330].strip() if len(line) >= 330 else ""

        if not tran_id:
            continue

        existing = db.query(Transaction).filter(Transaction.tran_id == tran_id).first()
        if existing:
            continue

        txn = Transaction(
            tran_id=tran_id,
            tran_type_cd=tran_type_cd,
            tran_cat_cd=tran_cat_cd,
            tran_source=tran_source,
            tran_desc=tran_desc,
            tran_amt=tran_amt,
            merchant_id=merchant_id,
            merchant_name=merchant_name,
            merchant_city=merchant_city,
            merchant_zip=merchant_zip,
            card_num=card_num,
            orig_ts=parse_timestamp(orig_ts_str),
            proc_ts=parse_timestamp(proc_ts_str),
        )
        db.add(txn)
        count += 1
    db.commit()
    print(f"  Loaded {count} transactions")


def seed_default_users(db: Session) -> None:
    """Create default admin and regular user accounts."""
    defaults = [
        ("admin", "Admin", "User", "admin123", "A"),
        ("user0001", "Regular", "User", "user1234", "U"),
    ]
    count = 0
    for uid, fname, lname, pwd, utype in defaults:
        existing = db.query(User).filter(User.user_id == uid).first()
        if existing:
            continue
        user = User(
            user_id=uid,
            first_name=fname,
            last_name=lname,
            password_hash=hash_password(pwd),
            user_type=utype,
        )
        db.add(user)
        count += 1
    db.commit()
    print(f"  Seeded {count} default users")


def run_migration() -> None:
    print("Creating database tables...")
    Base.metadata.create_all(bind=engine)
    print("Tables created.")

    db = SessionLocal()
    try:
        print("Loading seed data from app/data/ASCII/...")
        seed_default_users(db)
        load_accounts(db)
        load_customers(db)
        load_cards(db)
        load_card_xref(db)
        load_transaction_types(db)
        load_transaction_categories(db)
        load_disclosure_groups(db)
        load_tran_cat_bal(db)
        load_transactions(db)
        print("Data migration complete.")
    finally:
        db.close()


if __name__ == "__main__":
    run_migration()
