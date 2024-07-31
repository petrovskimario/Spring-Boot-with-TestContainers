package com.iwlabs.testcontainers.infrastructure.exception;

public class LowGpaException extends RuntimeException {
    public LowGpaException(String message) {
        super(message);
    }
}
