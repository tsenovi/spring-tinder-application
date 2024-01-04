package com.volasoftware.tinder.exceptions;

public class FriendNotFoundException extends RuntimeException {

    public FriendNotFoundException(String message) {
        super(message);
    }
}
