"""Batch job scheduler replacing JCL/Control-M orchestration.

Daily pipeline: POSTTRAN -> INTCALC -> CREASTMT -> TRANRPT
Uses APScheduler to replicate the mainframe scheduling pattern.
"""

import logging
import sys
from pathlib import Path

from apscheduler.schedulers.blocking import BlockingScheduler

sys.path.insert(0, str(Path(__file__).resolve().parent.parent))

from batch.account_processing import process_accounts
from batch.customer_processing import process_customers
from batch.interest_calculation import calculate_interest
from batch.statement_generation import generate_statements
from batch.transaction_posting import post_transactions

logger = logging.getLogger(__name__)


def daily_batch_pipeline():
    """Execute the daily batch pipeline in sequence."""
    logger.info("=== Starting daily batch pipeline ===")

    logger.info("Step 1: Transaction posting (POSTTRAN)")
    post_result = post_transactions()
    logger.info("  Result: %s", post_result)

    logger.info("Step 2: Interest calculation (INTCALC)")
    int_result = calculate_interest()
    logger.info("  Result: %s", int_result)

    logger.info("Step 3: Account processing")
    acct_result = process_accounts()
    logger.info("  Result: %s", acct_result)

    logger.info("Step 4: Customer processing")
    cust_result = process_customers()
    logger.info("  Result: %s", cust_result)

    logger.info("=== Daily batch pipeline complete ===")


def monthly_batch_pipeline():
    """Execute the monthly statement generation."""
    logger.info("=== Starting monthly statement generation ===")
    stmt_result = generate_statements()
    logger.info("  Result: %s", stmt_result)
    logger.info("=== Monthly statement generation complete ===")


def start_scheduler():
    """Start the APScheduler with configured jobs."""
    scheduler = BlockingScheduler()

    scheduler.add_job(
        daily_batch_pipeline,
        "cron",
        hour=2,
        minute=0,
        id="daily_batch",
        name="Daily Batch Pipeline",
    )

    scheduler.add_job(
        monthly_batch_pipeline,
        "cron",
        day=1,
        hour=3,
        minute=0,
        id="monthly_statements",
        name="Monthly Statement Generation",
    )

    logger.info("Batch scheduler started. Jobs: daily@02:00, monthly@03:00 on 1st")
    scheduler.start()


if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO, format="%(asctime)s %(name)s %(levelname)s %(message)s")
    start_scheduler()
