# Kafka CSFLE Producer

A Python application that connects to Apache Kafka and publishes messages using **Client-Side Field Level Encryption (CSFLE)**. Sensitive fields (e.g., card numbers, SSNs) are encrypted before being sent to Kafka, ensuring data protection at rest in the broker.

## Architecture

```
┌─────────────────────────────────────────────────────────┐
│                   CSFLE Producer                         │
│                                                         │
│  Message ──► Encrypt Fields ──► Serialize ──► Kafka     │
│              (AES-256-GCM)       (JSON)       Broker    │
│                                                         │
│  Envelope Encryption:                                   │
│  - Master Key (MEK) encrypts per-field Data Keys (DEK)  │
│  - Each field gets a unique DEK + random nonce          │
│  - Output: base64({ enc, dek, iv, ct, tag })            │
└─────────────────────────────────────────────────────────┘
```

## Quick Start

### Prerequisites

- Python 3.10+
- Apache Kafka broker (local or remote)
- `pip` or `uv` package manager

### Installation

```bash
cd kafka-csfle-producer

# Using pip
pip install -e ".[dev]"

# Or using uv
uv pip install -e ".[dev]"
```

### Configuration

Copy the example environment file and configure:

```bash
cp .env.example .env
```

Generate a master encryption key:

```bash
python -c "import os,base64; print(base64.b64encode(os.urandom(32)).decode())"
```

Set the key in `.env`:
```
CSFLE_MASTER_KEY=<your-generated-key>
```

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` | Kafka broker addresses |
| `KAFKA_TOPIC` | `carddemo.transactions` | Target topic |
| `KAFKA_CLIENT_ID` | `csfle-producer` | Producer client ID |
| `KAFKA_SECURITY_PROTOCOL` | `PLAINTEXT` | `PLAINTEXT`, `SSL`, `SASL_PLAINTEXT`, `SASL_SSL` |
| `KAFKA_SASL_MECHANISM` | (none) | `PLAIN`, `SCRAM-SHA-256`, `SCRAM-SHA-512` |
| `KAFKA_SASL_USERNAME` | (none) | SASL username / API key |
| `KAFKA_SASL_PASSWORD` | (none) | SASL password / API secret |
| `KAFKA_SSL_CA_LOCATION` | (none) | Path to CA certificate |
| `CSFLE_MASTER_KEY` | (required) | Base64-encoded 32-byte master key |
| `CSFLE_ENCRYPTED_FIELDS` | `card_number,customer_ssn,account_id` | Comma-separated fields to encrypt |

### Running

```bash
# Run the sample producer (publishes 5 test messages)
python -m src.main

# Or import and use programmatically
python -c "
from src.config import load_settings
from src.producer import CsfleKafkaProducer

settings = load_settings()
producer = CsfleKafkaProducer(settings)
producer.publish({'card_number': '4111111111111111', 'amount': 99.99}, key='txn-001')
producer.flush()
"
```

### Running Tests

```bash
pytest tests/ -v
```

### Linting & Type Checking

```bash
ruff check src/ tests/
mypy src/
```

## Encryption Details

### Algorithm
- **AES-256-GCM** (authenticated encryption with associated data)
- 256-bit keys, 96-bit nonces, 128-bit authentication tags

### Envelope Encryption
Each field value is encrypted with a unique **Data Encryption Key (DEK)**. The DEK itself is encrypted with the **Master Encryption Key (MEK)**. This allows key rotation at the MEK level without re-encrypting all data.

### Wire Format
Encrypted fields are stored as base64-encoded JSON envelopes:
```json
{
  "enc": "AES-256-GCM",
  "dek": "<base64: nonce + encrypted DEK>",
  "iv": "<base64: 12-byte field nonce>",
  "ct": "<base64: ciphertext>",
  "tag": "<base64: 16-byte auth tag>"
}
```

### Decryption
A consumer with the same master key can decrypt fields:
```python
from src.encryption import CsfleEncryptor

encryptor = CsfleEncryptor(master_key_b64="<your-key>")
plaintext = encryptor.decrypt_field(encrypted_field_value)
```

## Integration with CardDemo

This producer is designed to publish CardDemo transaction events with sensitive fields (card numbers, SSNs, account IDs) encrypted before reaching Kafka. Downstream consumers that possess the master key can decrypt the fields; all others see only encrypted blobs.
