package com.cardemo.exception;

/**
 * Exception thrown for authentication failures.
 * Replaces the COSGN00C error handling for wrong password and user not found.
 */
public class AuthenticationException extends RuntimeException {

    public AuthenticationException(String message) {
        super(message);
    }
}
