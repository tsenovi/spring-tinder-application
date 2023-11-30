package com.volasoftware.tinder.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EmailIsTakenException extends RuntimeException {

    public EmailIsTakenException(String message) {
        super(message);
    }
}
