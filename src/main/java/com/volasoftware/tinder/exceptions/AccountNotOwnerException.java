package com.volasoftware.tinder.exceptions;

public class AccountNotOwnerException extends RuntimeException {

    public AccountNotOwnerException(String message) {
        super(message);
    }
}
