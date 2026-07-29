"""Transaction posting batch service.

Replaces CBTRN01C.cbl, CBTRN02C.cbl, CBTRN03C.cbl.
Reads unposted transactions, validates, and updates account balances.
"""

import logging
from datetime import datetime
from decimal import Decimal

from sqlalchemy.orm import Session

from app.database import SessionLocal
from app.models.models import Account, Card, CardXref, TranCatBal, Transaction

logger = logging.getLogger(__name__)


def post_transactions(db: Session | None = None) -> dict:
    """Process unposted transactions and update account balances."""
    own_session = db is None
    if own_session:
        db = SessionLocal()

    try:
        unposted = (
            db.query(Transaction)
            .filter(Transaction.proc_ts.is_(None))
            .order_by(Transaction.orig_ts)
            .all()
        )

        posted_count = 0
        rejected_count = 0
        total_amount = Decimal("0.00")

        for txn in unposted:
            xref = (
                db.query(CardXref)
                .filter(CardXref.xref_card_num == txn.card_num)
                .first()
            )
            if not xref:
                logger.warning("No xref for card %s, skipping txn %s", txn.card_num, txn.tran_id)
                rejected_count += 1
                continue

            account = db.query(Account).filter(Account.acct_id == xref.xref_acct_id).first()
            if not account:
                logger.warning("No account %s for txn %s", xref.xref_acct_id, txn.tran_id)
                rejected_count += 1
                continue

            card = db.query(Card).filter(Card.card_num == txn.card_num).first()
            if not card or card.active_status != "Y":
                logger.warning("Card %s inactive, skipping txn %s", txn.card_num, txn.tran_id)
                rejected_count += 1
                continue

            if account.active_status != "Y":
                logger.warning("Account %s inactive, skipping txn %s", account.acct_id, txn.tran_id)
                rejected_count += 1
                continue

            txn_amt = Decimal(str(txn.tran_amt))

            if txn.tran_type_cd in ("01", "04"):
                account.curr_bal = Decimal(str(account.curr_bal)) + txn_amt
                account.curr_cyc_debit = Decimal(str(account.curr_cyc_debit)) + txn_amt
            elif txn.tran_type_cd in ("02", "03", "05"):
                account.curr_bal = Decimal(str(account.curr_bal)) - abs(txn_amt)
                account.curr_cyc_credit = Decimal(str(account.curr_cyc_credit)) + abs(txn_amt)
            else:
                account.curr_bal = Decimal(str(account.curr_bal)) + txn_amt

            cat_bal = (
                db.query(TranCatBal)
                .filter(
                    TranCatBal.acct_id == account.acct_id,
                    TranCatBal.tran_type_cd == txn.tran_type_cd,
                    TranCatBal.tran_cat_cd == txn.tran_cat_cd,
                )
                .first()
            )
            if cat_bal:
                cat_bal.cat_bal = Decimal(str(cat_bal.cat_bal)) + txn_amt
            else:
                cat_bal = TranCatBal(
                    acct_id=account.acct_id,
                    tran_type_cd=txn.tran_type_cd,
                    tran_cat_cd=txn.tran_cat_cd,
                    cat_bal=txn_amt,
                )
                db.add(cat_bal)

            txn.proc_ts = datetime.utcnow()
            posted_count += 1
            total_amount += txn_amt

        db.commit()
        result = {
            "posted": posted_count,
            "rejected": rejected_count,
            "total_amount": str(total_amount),
            "timestamp": datetime.utcnow().isoformat(),
        }
        logger.info("Transaction posting complete: %s", result)
        return result

    finally:
        if own_session:
            db.close()


if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO)
    result = post_transactions()
    print(f"Result: {result}")
