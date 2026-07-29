"""Interest calculation batch service.

Replaces CBACT04C.cbl.
Calculates monthly interest on account balances using disclosure group rates.
"""

import logging
from datetime import datetime
from decimal import Decimal, ROUND_HALF_UP

from sqlalchemy.orm import Session

from app.database import SessionLocal
from app.models.models import Account, DisclosureGroup, TranCatBal

logger = logging.getLogger(__name__)


def calculate_interest(db: Session | None = None) -> dict:
    """Calculate and apply interest to all active accounts."""
    own_session = db is None
    if own_session:
        db = SessionLocal()

    try:
        accounts = db.query(Account).filter(Account.active_status == "Y").all()
        processed_count = 0
        total_interest = Decimal("0.00")

        for account in accounts:
            if not account.group_id or not account.group_id.strip():
                continue

            cat_bals = (
                db.query(TranCatBal)
                .filter(TranCatBal.acct_id == account.acct_id)
                .all()
            )

            account_interest = Decimal("0.00")
            for cat_bal in cat_bals:
                disc_group = (
                    db.query(DisclosureGroup)
                    .filter(
                        DisclosureGroup.acct_group_id == account.group_id,
                        DisclosureGroup.tran_type_cd == cat_bal.tran_type_cd,
                        DisclosureGroup.tran_cat_cd == cat_bal.tran_cat_cd,
                    )
                    .first()
                )
                if not disc_group:
                    continue

                rate = Decimal(str(disc_group.interest_rate))
                balance = Decimal(str(cat_bal.cat_bal))
                if balance <= 0:
                    continue

                monthly_rate = rate / Decimal("1200")
                interest = (balance * monthly_rate).quantize(
                    Decimal("0.01"), rounding=ROUND_HALF_UP
                )
                account_interest += interest

            if account_interest > 0:
                account.curr_bal = (
                    Decimal(str(account.curr_bal)) + account_interest
                ).quantize(Decimal("0.01"), rounding=ROUND_HALF_UP)
                total_interest += account_interest
                processed_count += 1

        db.commit()
        result = {
            "accounts_processed": processed_count,
            "total_interest": str(total_interest),
            "timestamp": datetime.utcnow().isoformat(),
        }
        logger.info("Interest calculation complete: %s", result)
        return result

    finally:
        if own_session:
            db.close()


if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO)
    result = calculate_interest()
    print(f"Result: {result}")
