package com.programmers.dev.exception;


public class CreamException extends RuntimeException {
    private final ErrorCode errorCode;

    public CreamException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
