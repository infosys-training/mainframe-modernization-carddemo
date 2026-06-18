"""Account batch processing service.

Replaces CBACT01C.cbl, CBACT02C.cbl, CBACT03C.cbl.
Performs batch account file operations (validation, status updates, reporting).
"""

import logging
from datetime import date, datetime
from decimal import Decimal

from sqlalchemy.orm import Session

from app.database import SessionLocal
from app.models.models import Account

logger = logging.getLogger(__name__)


def process_accounts(db: Session | None = None) -> dict:
    """Batch process accounts: validate, flag expired, generate summary."""
    own_session = db is None
    if own_session:
        db = SessionLocal()

    try:
        accounts = db.query(Account).all()
        today = date.today()

        expired_count = 0
        over_limit_count = 0
        active_count = 0
        total_balance = Decimal("0.00")

        for account in accounts:
            if account.active_status == "Y":
                active_count += 1

            if account.expiration_date and account.expiration_date < today:
                if account.active_status == "Y":
                    account.active_status = "N"
                    expired_count += 1
                    logger.info("Account %s expired, deactivated", account.acct_id)

            curr_bal = Decimal(str(account.curr_bal))
            credit_limit = Decimal(str(account.credit_limit))
            if curr_bal > credit_limit and credit_limit > 0:
                over_limit_count += 1
                logger.warning(
                    "Account %s over limit: bal=%s limit=%s",
                    account.acct_id,
                    curr_bal,
                    credit_limit,
                )

            total_balance += curr_bal

        db.commit()
        result = {
            "total_accounts": len(accounts),
            "active_accounts": active_count,
            "expired_deactivated": expired_count,
            "over_limit": over_limit_count,
            "total_balance": str(total_balance),
            "timestamp": datetime.utcnow().isoformat(),
        }
        logger.info("Account processing complete: %s", result)
        return result

    finally:
        if own_session:
            db.close()


if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO)
    result = process_accounts()
    print(f"Result: {result}")
