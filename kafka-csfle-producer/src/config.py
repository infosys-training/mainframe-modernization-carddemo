"""Application configuration loaded from environment variables."""

from pydantic_settings import BaseSettings


class KafkaSettings(BaseSettings):
    """Kafka connection settings."""

    bootstrap_servers: str = "localhost:9092"
    topic: str = "carddemo.transactions"
    client_id: str = "csfle-producer"

    # Security (optional — leave blank for plaintext)
    security_protocol: str = "PLAINTEXT"
    sasl_mechanism: str | None = None
    sasl_username: str | None = None
    sasl_password: str | None = None
    ssl_ca_location: str | None = None

    model_config = {"env_prefix": "KAFKA_"}


class CsfleSettings(BaseSettings):
    """Client-Side Field Level Encryption settings."""

    # Base64-encoded 256-bit master key (32 bytes → 44 chars base64)
    # Generate with: python -c "import os,base64; print(base64.b64encode(os.urandom(32)).decode())"
    master_key: str = ""

    # Fields to encrypt (comma-separated dot-notation paths)
    encrypted_fields: str = "card_number,customer_ssn,account_id"

    model_config = {"env_prefix": "CSFLE_"}

    @property
    def field_list(self) -> list[str]:
        """Return encrypted fields as a list."""
        return [f.strip() for f in self.encrypted_fields.split(",") if f.strip()]


class AppSettings(BaseSettings):
    """Top-level application settings."""

    kafka: KafkaSettings = KafkaSettings()
    csfle: CsfleSettings = CsfleSettings()


def load_settings() -> AppSettings:
    """Load settings from environment variables."""
    return AppSettings()
