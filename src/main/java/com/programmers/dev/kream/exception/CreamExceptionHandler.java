package com.programmers.dev.kream.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
public class CreamExceptionHandler {

    @ExceptionHandler(CreamException.class)
    public ResponseEntity<ErrorResponse> handleException(CreamException creamException) {
        return ResponseEntity
                .status(creamException.getErrorCode().getHttpStatus())
                .body(ErrorResponse.of(creamException.getErrorCode()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(INTERNAL_SERVER_ERROR.value(), "something went wrong on server"));
    }
}
