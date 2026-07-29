"""Statement generation batch service.

Replaces CBSTM03A.CBL and CBSTM03B.CBL.
Generates monthly statements for all active accounts.
"""

import logging
from datetime import date, datetime, timedelta
from decimal import Decimal
from pathlib import Path

from jinja2 import Template
from sqlalchemy.orm import Session

from app.database import SessionLocal
from app.models.models import Account, CardXref, Customer, Transaction

logger = logging.getLogger(__name__)

STATEMENT_TEMPLATE = """\
================================================================================
                         CARDDEMO MONTHLY STATEMENT
================================================================================
Account Number: {{ acct_id }}
Customer Name:  {{ customer_name }}
Statement Date: {{ statement_date }}
--------------------------------------------------------------------------------
Current Balance:    ${{ "%.2f"|format(curr_bal) }}
Credit Limit:       ${{ "%.2f"|format(credit_limit) }}
Available Credit:   ${{ "%.2f"|format(available_credit) }}
Cycle Credits:      ${{ "%.2f"|format(cycle_credits) }}
Cycle Debits:       ${{ "%.2f"|format(cycle_debits) }}
--------------------------------------------------------------------------------
TRANSACTIONS
--------------------------------------------------------------------------------
{% for txn in transactions -%}
{{ txn.date }}  {{ "%-40s"|format(txn.description) }}  ${{ "%10.2f"|format(txn.amount) }}
{% endfor -%}
--------------------------------------------------------------------------------
Total Transactions: {{ transactions|length }}
================================================================================
"""


def generate_statements(
    output_dir: str = "statements",
    db: Session | None = None,
) -> dict:
    """Generate monthly statements for all active accounts."""
    own_session = db is None
    if own_session:
        db = SessionLocal()

    try:
        out_path = Path(output_dir)
        out_path.mkdir(parents=True, exist_ok=True)

        accounts = db.query(Account).filter(Account.active_status == "Y").all()
        today = date.today()
        period_start = today.replace(day=1) - timedelta(days=1)
        period_start = period_start.replace(day=1)

        generated_count = 0
        template = Template(STATEMENT_TEMPLATE)

        for account in accounts:
            xref = (
                db.query(CardXref)
                .filter(CardXref.xref_acct_id == account.acct_id)
                .first()
            )
            customer_name = "N/A"
            if xref:
                customer = (
                    db.query(Customer)
                    .filter(Customer.cust_id == xref.xref_cust_id)
                    .first()
                )
                if customer:
                    customer_name = f"{customer.first_name} {customer.last_name}".strip()

            card_nums = [
                x.xref_card_num
                for x in db.query(CardXref)
                .filter(CardXref.xref_acct_id == account.acct_id)
                .all()
            ]

            txns = (
                db.query(Transaction)
                .filter(Transaction.card_num.in_(card_nums))
                .order_by(Transaction.orig_ts)
                .all()
            )

            curr_bal = float(account.curr_bal)
            credit_limit = float(account.credit_limit)

            txn_list = []
            for txn in txns:
                txn_list.append(
                    {
                        "date": txn.orig_ts.strftime("%Y-%m-%d") if txn.orig_ts else "N/A",
                        "description": txn.tran_desc[:40],
                        "amount": float(txn.tran_amt),
                    }
                )

            stmt_text = template.render(
                acct_id=account.acct_id,
                customer_name=customer_name,
                statement_date=today.strftime("%Y-%m-%d"),
                curr_bal=curr_bal,
                credit_limit=credit_limit,
                available_credit=credit_limit - curr_bal,
                cycle_credits=float(account.curr_cyc_credit),
                cycle_debits=float(account.curr_cyc_debit),
                transactions=txn_list,
            )

            stmt_file = out_path / f"statement_{account.acct_id}_{today.strftime('%Y%m%d')}.txt"
            stmt_file.write_text(stmt_text)
            generated_count += 1

        result = {
            "statements_generated": generated_count,
            "output_directory": str(out_path),
            "timestamp": datetime.utcnow().isoformat(),
        }
        logger.info("Statement generation complete: %s", result)
        return result

    finally:
        if own_session:
            db.close()


if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO)
    result = generate_statements()
    print(f"Result: {result}")
