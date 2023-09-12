package com.programmers.dev.exception;

public record ErrorResponse(
        Integer code,
        String description
) {
    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getCode(), errorCode.getDescription());
    }
}
