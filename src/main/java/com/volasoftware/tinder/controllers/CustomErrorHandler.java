package com.volasoftware.tinder.controllers;

import com.volasoftware.tinder.exceptions.*;
import com.volasoftware.tinder.responses.ResponseHandler;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({AccountNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?> handleNotFoundException(RuntimeException exception) {
        return ResponseHandler.generateResponse(exception.getMessage(), HttpStatus.NOT_FOUND, null);
    }

    @ExceptionHandler({
        EmailIsTakenException.class,
        AccountNotVerifiedException.class,
        VerificationTokenExpiredException.class,
        AccountNotOwnerException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleBadRequestException(RuntimeException exception) {
        return ResponseHandler.generateResponse(exception.getMessage(), HttpStatus.BAD_REQUEST,
            null);
    }

    @ExceptionHandler({
        EmailIsNotValidException.class,
        EmailAlreadyVerifiedException.class
    })
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ResponseEntity<?> handleNotAcceptableException(RuntimeException exception) {
        return ResponseHandler.generateResponse(exception.getMessage(), HttpStatus.NOT_ACCEPTABLE,
            null);
    }

    @ExceptionHandler({AuthenticationException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<?> handleAuthenticationException(RuntimeException exception) {
        return ResponseHandler.generateResponse(exception.getMessage(), HttpStatus.FORBIDDEN, null);
    }
}
