package com.cardemo.exception;

/**
 * Exception thrown when a requested resource is not found.
 * Replaces COBOL DFHRESP(NOTFND) handling patterns found in programs like
 * COTRN01C, COBIL00C, COCRDSLC etc.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceType, String id) {
        super(resourceType + " not found with ID: " + id);
    }
}
