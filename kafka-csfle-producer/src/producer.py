"""Kafka producer with Client-Side Field Level Encryption."""

import json
import logging
from typing import Any

from confluent_kafka import KafkaError, Producer

from .config import AppSettings
from .encryption import CsfleEncryptor

logger = logging.getLogger(__name__)


class CsfleKafkaProducer:
    """Kafka producer that encrypts specified fields before publishing."""

    def __init__(self, settings: AppSettings) -> None:
        self._settings = settings
        self._topic = settings.kafka.topic
        self._encrypted_fields = settings.csfle.field_list

        # Initialize CSFLE encryptor
        if not settings.csfle.master_key:
            raise ValueError(
                "CSFLE_MASTER_KEY environment variable must be set. "
                'Generate with: python -c "import os,base64;'
                ' print(base64.b64encode(os.urandom(32)).decode())"'
            )
        self._encryptor = CsfleEncryptor(settings.csfle.master_key)

        # Build Kafka producer config
        kafka_config: dict[str, str] = {
            "bootstrap.servers": settings.kafka.bootstrap_servers,
            "client.id": settings.kafka.client_id,
        }

        if settings.kafka.security_protocol != "PLAINTEXT":
            kafka_config["security.protocol"] = settings.kafka.security_protocol

        if settings.kafka.sasl_mechanism:
            kafka_config["sasl.mechanism"] = settings.kafka.sasl_mechanism

        if settings.kafka.sasl_username:
            kafka_config["sasl.username"] = settings.kafka.sasl_username

        if settings.kafka.sasl_password:
            kafka_config["sasl.password"] = settings.kafka.sasl_password

        if settings.kafka.ssl_ca_location:
            kafka_config["ssl.ca.location"] = settings.kafka.ssl_ca_location

        self._producer = Producer(kafka_config)
        logger.info(
            "CSFLE Kafka producer initialized (topic=%s, encrypted_fields=%s)",
            self._topic,
            self._encrypted_fields,
        )

    def _delivery_callback(self, err: KafkaError | None, msg: Any) -> None:
        """Called once for each message produced to indicate delivery result."""
        if err is not None:
            logger.error("Message delivery failed: %s", err)
        else:
            logger.info(
                "Message delivered to %s [partition=%d, offset=%d]",
                msg.topic(),
                msg.partition(),
                msg.offset(),
            )

    def publish(
        self,
        message: dict[str, Any],
        key: str | None = None,
        headers: dict[str, str] | None = None,
    ) -> None:
        """Encrypt specified fields and publish the message to Kafka.

        Args:
            message: The message payload as a dictionary.
            key: Optional message key for partitioning.
            headers: Optional message headers.
        """
        # Encrypt sensitive fields
        encrypted_message = self._encryptor.encrypt_message(message, self._encrypted_fields)

        # Serialize to JSON
        value = json.dumps(encrypted_message).encode("utf-8")

        # Build Kafka headers
        kafka_headers: list[tuple[str, str | bytes | None]] | None = None
        if headers:
            kafka_headers = [(k, v.encode("utf-8")) for k, v in headers.items()]

        # Produce the message
        self._producer.produce(
            topic=self._topic,
            key=key.encode("utf-8") if key else None,
            value=value,
            headers=kafka_headers,
            callback=self._delivery_callback,
        )
        self._producer.poll(0)

    def flush(self, timeout: float = 10.0) -> int:
        """Wait for all messages to be delivered.

        Returns the number of messages still in the queue (0 means all delivered).
        """
        remaining = self._producer.flush(timeout)
        if remaining > 0:
            logger.warning("%d messages still in queue after flush timeout", remaining)
        return remaining

    def close(self) -> None:
        """Flush and close the producer."""
        self.flush()
        logger.info("CSFLE Kafka producer closed")
