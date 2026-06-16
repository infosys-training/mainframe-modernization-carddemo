"""Tests for the CSFLE encryption module."""

import base64
import os

import pytest

from src.encryption import CsfleEncryptor


@pytest.fixture
def master_key() -> str:
    """Generate a test master key."""
    return base64.b64encode(os.urandom(32)).decode()


@pytest.fixture
def encryptor(master_key: str) -> CsfleEncryptor:
    """Create a test encryptor."""
    return CsfleEncryptor(master_key)


class TestCsfleEncryptor:
    def test_encrypt_decrypt_roundtrip(self, encryptor: CsfleEncryptor) -> None:
        """Encrypting then decrypting returns the original value."""
        plaintext = "4111111111111111"
        encrypted = encryptor.encrypt_field(plaintext)
        decrypted = encryptor.decrypt_field(encrypted)
        assert decrypted == plaintext

    def test_encrypted_value_differs_from_plaintext(self, encryptor: CsfleEncryptor) -> None:
        """Encrypted output should not contain the plaintext."""
        plaintext = "123-45-6789"
        encrypted = encryptor.encrypt_field(plaintext)
        assert plaintext not in encrypted

    def test_each_encryption_produces_unique_output(self, encryptor: CsfleEncryptor) -> None:
        """Two encryptions of the same value produce different ciphertexts."""
        plaintext = "SENSITIVE_DATA"
        enc1 = encryptor.encrypt_field(plaintext)
        enc2 = encryptor.encrypt_field(plaintext)
        assert enc1 != enc2

    def test_wrong_key_fails_decryption(self, encryptor: CsfleEncryptor) -> None:
        """Decrypting with a different key should fail."""
        plaintext = "SECRET"
        encrypted = encryptor.encrypt_field(plaintext)

        wrong_key = base64.b64encode(os.urandom(32)).decode()
        wrong_encryptor = CsfleEncryptor(wrong_key)

        with pytest.raises(Exception):
            wrong_encryptor.decrypt_field(encrypted)

    def test_invalid_master_key_length(self) -> None:
        """Master key must be exactly 32 bytes."""
        short_key = base64.b64encode(os.urandom(16)).decode()
        with pytest.raises(ValueError, match="32 bytes"):
            CsfleEncryptor(short_key)

    def test_encrypt_message_encrypts_specified_fields(self, encryptor: CsfleEncryptor) -> None:
        """Only specified fields are encrypted in a message dict."""
        message = {
            "card_number": "4111111111111111",
            "customer_ssn": "123-45-6789",
            "description": "Normal purchase",
            "amount": 100.50,
        }

        result = encryptor.encrypt_message(message, ["card_number", "customer_ssn"])

        # Encrypted fields should be different from original
        assert result["card_number"] != "4111111111111111"
        assert result["customer_ssn"] != "123-45-6789"

        # Non-encrypted fields should remain unchanged
        assert result["description"] == "Normal purchase"
        assert result["amount"] == 100.50

        # Should be decryptable
        assert encryptor.decrypt_field(result["card_number"]) == "4111111111111111"
        assert encryptor.decrypt_field(result["customer_ssn"]) == "123-45-6789"

    def test_encrypt_message_skips_missing_fields(self, encryptor: CsfleEncryptor) -> None:
        """Fields not present in the message are skipped without error."""
        message = {"card_number": "4111111111111111"}
        result = encryptor.encrypt_message(message, ["card_number", "nonexistent_field"])
        assert encryptor.decrypt_field(result["card_number"]) == "4111111111111111"

    def test_encrypt_message_skips_none_values(self, encryptor: CsfleEncryptor) -> None:
        """None values are not encrypted."""
        message = {"card_number": None, "account_id": "12345"}
        result = encryptor.encrypt_message(message, ["card_number", "account_id"])
        assert result["card_number"] is None
        assert encryptor.decrypt_field(result["account_id"]) == "12345"
