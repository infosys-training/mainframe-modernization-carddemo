"""Customer batch processing service.

Replaces CBCUS01C.cbl.
Performs batch customer data processing and validation.
"""

import logging
from datetime import datetime

from sqlalchemy.orm import Session

from app.database import SessionLocal
from app.models.models import Customer

logger = logging.getLogger(__name__)

US_STATE_CODES = {
    "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA",
    "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD",
    "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ",
    "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC",
    "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY",
    "DC",
}


def process_customers(db: Session | None = None) -> dict:
    """Validate and process all customer records."""
    own_session = db is None
    if own_session:
        db = SessionLocal()

    try:
        customers = db.query(Customer).all()
        valid_count = 0
        invalid_state = 0
        missing_ssn = 0
        missing_name = 0

        for customer in customers:
            is_valid = True

            if not customer.first_name.strip() or not customer.last_name.strip():
                missing_name += 1
                is_valid = False

            if not customer.ssn or not customer.ssn.strip():
                missing_ssn += 1
                is_valid = False

            state = customer.addr_state_cd.strip().upper()
            if state and state not in US_STATE_CODES:
                invalid_state += 1
                is_valid = False

            if is_valid:
                valid_count += 1

        result = {
            "total_customers": len(customers),
            "valid_customers": valid_count,
            "invalid_state_code": invalid_state,
            "missing_ssn": missing_ssn,
            "missing_name": missing_name,
            "timestamp": datetime.utcnow().isoformat(),
        }
        logger.info("Customer processing complete: %s", result)
        return result

    finally:
        if own_session:
            db.close()


if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO)
    result = process_customers()
    print(f"Result: {result}")
