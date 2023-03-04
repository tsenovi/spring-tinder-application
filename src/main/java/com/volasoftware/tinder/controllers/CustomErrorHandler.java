package com.volasoftware.tinder.controllers;

import com.volasoftware.tinder.dtos.ErrorResponseDTO;
import com.volasoftware.tinder.exceptions.AccountNotFoundException;
import com.volasoftware.tinder.exceptions.EmailIsTakenException;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomErrorHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler({AccountNotFoundException.class})
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<ErrorResponseDTO> handleNotFoundException(RuntimeException exception) {
    return new ResponseEntity<>(
        new ErrorResponseDTO(
            HttpStatus.NOT_FOUND,
            exception.getMessage(),
            LocalDateTime.now()),
        HttpStatus.NOT_FOUND
    );
  }

  @ExceptionHandler({EmailIsTakenException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorResponseDTO> handleBadRequestException(RuntimeException exception) {
    return new ResponseEntity<>(
        new ErrorResponseDTO(
            HttpStatus.BAD_REQUEST,
            exception.getMessage(),
            LocalDateTime.now()),
        HttpStatus.BAD_REQUEST
    );
  }
}
