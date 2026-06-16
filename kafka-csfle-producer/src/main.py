"""Main entry point — demonstrates publishing encrypted messages to Kafka."""

import logging
import sys
from datetime import datetime, timezone
from typing import Any

from .config import load_settings
from .producer import CsfleKafkaProducer

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(name)s: %(message)s",
)
logger = logging.getLogger(__name__)


def create_sample_transaction() -> dict[str, Any]:
    """Create a sample CardDemo transaction message with sensitive fields."""
    return {
        "transaction_id": "TXN-2024-00001234",
        "card_number": "4111111111111111",
        "account_id": "12345678901",
        "customer_ssn": "123-45-6789",
        "type_code": "PR",
        "category_code": "5411",
        "source": "ONLINE",
        "description": "Grocery purchase at SuperMart",
        "amount": 125.99,
        "original_date": datetime.now(timezone.utc).strftime("%Y-%m-%d"),
        "processed_date": datetime.now(timezone.utc).strftime("%Y-%m-%d"),
        "merchant": {
            "id": "MERCH0001",
            "name": "SuperMart Groceries",
            "city": "New York",
            "zip": "10001",
        },
    }


def main() -> None:
    """Publish sample encrypted messages to Kafka."""
    settings = load_settings()

    logger.info("Starting CSFLE Kafka Producer")
    logger.info("  Kafka broker: %s", settings.kafka.bootstrap_servers)
    logger.info("  Topic: %s", settings.kafka.topic)
    logger.info("  Encrypted fields: %s", settings.csfle.field_list)

    try:
        producer = CsfleKafkaProducer(settings)
    except ValueError as e:
        logger.error("Configuration error: %s", e)
        sys.exit(1)

    # Publish sample messages
    num_messages = 5
    logger.info("Publishing %d sample transaction messages...", num_messages)

    for i in range(num_messages):
        message = create_sample_transaction()
        message["transaction_id"] = f"TXN-2024-{i + 1:08d}"
        message["amount"] = round(50.0 + i * 25.50, 2)

        logger.info(
            "Publishing message %d: transaction_id=%s, amount=%.2f",
            i + 1,
            message["transaction_id"],
            message["amount"],
        )

        producer.publish(
            message=message,
            key=message["transaction_id"],
            headers={"source": "carddemo-csfle-producer", "version": "1.0"},
        )

    # Wait for delivery
    remaining = producer.flush()
    if remaining == 0:
        logger.info("All %d messages delivered successfully", num_messages)
    else:
        logger.warning("%d messages failed to deliver", remaining)

    producer.close()


if __name__ == "__main__":
    main()
