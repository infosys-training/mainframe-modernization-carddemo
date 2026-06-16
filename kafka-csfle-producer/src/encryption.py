"""Client-Side Field Level Encryption (CSFLE) using AES-256-GCM.

Implements envelope encryption:
- A master key (MEK) is used to encrypt/decrypt per-field data encryption keys (DEKs).
- Each field value is encrypted with a unique DEK + random nonce.
- The encrypted output includes the encrypted DEK, nonce, ciphertext, and auth tag.

Wire format (base64-encoded JSON object):
{
    "enc": "AES-256-GCM",
    "dek": "<base64 encrypted DEK>",
    "iv": "<base64 nonce>",
    "ct": "<base64 ciphertext>",
    "tag": "<base64 auth tag>"
}
"""

import base64
import json
import os
from typing import Any

from cryptography.hazmat.primitives.ciphers.aead import AESGCM


class CsfleEncryptor:
    """Encrypts individual field values using AES-256-GCM with envelope encryption."""

    def __init__(self, master_key_b64: str) -> None:
        """Initialize with a base64-encoded 256-bit master key."""
        self._master_key = base64.b64decode(master_key_b64)
        if len(self._master_key) != 32:
            raise ValueError("Master key must be exactly 32 bytes (256 bits)")
        self._master_cipher = AESGCM(self._master_key)

    def encrypt_field(self, plaintext: str) -> str:
        """Encrypt a single field value.

        Returns a base64-encoded JSON envelope containing the encrypted DEK,
        nonce, ciphertext, and authentication tag.
        """
        # Generate a random Data Encryption Key (DEK) for this field
        dek = os.urandom(32)
        dek_nonce = os.urandom(12)

        # Encrypt the DEK with the master key
        encrypted_dek = self._master_cipher.encrypt(dek_nonce, dek, None)

        # Encrypt the field value with the DEK
        field_cipher = AESGCM(dek)
        field_nonce = os.urandom(12)
        plaintext_bytes = plaintext.encode("utf-8")
        ciphertext_and_tag = field_cipher.encrypt(field_nonce, plaintext_bytes, None)

        # AES-GCM appends the 16-byte tag to ciphertext
        ciphertext = ciphertext_and_tag[:-16]
        tag = ciphertext_and_tag[-16:]

        envelope = {
            "enc": "AES-256-GCM",
            "dek": base64.b64encode(dek_nonce + encrypted_dek).decode(),
            "iv": base64.b64encode(field_nonce).decode(),
            "ct": base64.b64encode(ciphertext).decode(),
            "tag": base64.b64encode(tag).decode(),
        }
        return base64.b64encode(json.dumps(envelope).encode()).decode()

    def decrypt_field(self, encrypted_value: str) -> str:
        """Decrypt a single field value from its base64-encoded envelope."""
        envelope = json.loads(base64.b64decode(encrypted_value))

        # Extract and decrypt the DEK
        dek_blob = base64.b64decode(envelope["dek"])
        dek_nonce = dek_blob[:12]
        encrypted_dek = dek_blob[12:]
        dek = self._master_cipher.decrypt(dek_nonce, encrypted_dek, None)

        # Decrypt the field value
        field_cipher = AESGCM(dek)
        field_nonce = base64.b64decode(envelope["iv"])
        ciphertext = base64.b64decode(envelope["ct"])
        tag = base64.b64decode(envelope["tag"])
        plaintext_bytes = field_cipher.decrypt(field_nonce, ciphertext + tag, None)

        return plaintext_bytes.decode("utf-8")

    def encrypt_message(
        self, message: dict[str, Any], fields_to_encrypt: list[str]
    ) -> dict[str, Any]:
        """Encrypt specified fields within a message dict.

        Supports dot-notation for nested fields (e.g., 'customer.ssn').
        Fields that don't exist in the message are skipped.
        """
        result: dict[str, Any] = json.loads(json.dumps(message))

        for field_path in fields_to_encrypt:
            parts = field_path.split(".")
            obj = result
            for part in parts[:-1]:
                if isinstance(obj, dict) and part in obj:
                    obj = obj[part]
                else:
                    break
            else:
                final_key = parts[-1]
                if isinstance(obj, dict) and final_key in obj:
                    value = obj[final_key]
                    if value is not None:
                        obj[final_key] = self.encrypt_field(str(value))

        return result
