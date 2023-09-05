package com.programmers.dev.kream.exception;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public enum ErrorCode {

    INVALID_ID(BAD_REQUEST, BAD_REQUEST.value(), "id does not exist in database. please check again"),
    NO_AUTHENTICATION(UNAUTHORIZED, UNAUTHORIZED.value(), "no authentication. please log in"),
    NO_AUTHORITY(FORBIDDEN, FORBIDDEN.value(), "you have no authorization for this access"),
    ;


    private final HttpStatus httpStatus;
    private final Integer code;
    private final String description;

    ErrorCode(HttpStatus httpStatus, Integer code, String description) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.description = description;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
