package com.volasoftware.tinder.exceptions;

public class VerificationTokenNotExistException extends RuntimeException {
    public VerificationTokenNotExistException(String message) {
        super(message);
    }
}
