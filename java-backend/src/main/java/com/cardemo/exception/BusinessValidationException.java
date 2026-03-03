package com.cardemo.exception;

/**
 * Exception thrown for business logic validation failures.
 * Replaces the WS-ERR-FLG / ERR-FLG-ON patterns and validation EVALUATE blocks
 * found throughout the original COBOL programs (COTRN02C, COBIL00C, COACTUPC, etc.).
 */
public class BusinessValidationException extends RuntimeException {

    public BusinessValidationException(String message) {
        super(message);
    }
}
