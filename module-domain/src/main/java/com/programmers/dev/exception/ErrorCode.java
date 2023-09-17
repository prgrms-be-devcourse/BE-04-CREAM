package com.programmers.dev.exception;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public enum ErrorCode {

    INVALID_ID(BAD_REQUEST, BAD_REQUEST.value(), "id does not exist in database. please check again"),
    BAD_BUSINESS_LOGIC(BAD_REQUEST, BAD_REQUEST.value(), "invalid logic for this service"),
    NO_AUTHENTICATION(UNAUTHORIZED, -101, "no authentication. please log in"),
    NO_AUTHORITY(FORBIDDEN, -102, "you have no authorization for this access"),
    INVALID_LOGIN_INFO(BAD_REQUEST, -103, "invalid email or password."),
    INVALID_SESSION_FORMAT(BAD_REQUEST, -104, "invalid session format"),
    SESSION_EXPIRATION(BAD_REQUEST, -105, "session has expired."),
    INVALID_AUCTION_BIDDING(BAD_REQUEST, BAD_REQUEST.value(),"this is not the time to bid for an auction"),
    INVALID_REQUEST_VALUE(BAD_REQUEST, BAD_REQUEST.value(),"please check request value again"),
    AFTER_DUE_DATE(BAD_REQUEST, BAD_REQUEST.value(), "biding is expired."),
    OVER_PRICE(BAD_REQUEST, BAD_REQUEST.value(), "too much bidding price"),
    INSUFFICIENT_ACCOUNT_MONEY(BAD_REQUEST, -201, "not enough account money"),
    INVALID_BIDDING_PRICE(BAD_REQUEST,BAD_REQUEST.value(),"please enter a higher price than the highest bid price"),
    INVALID_CHANGE_STATUS(BAD_REQUEST,BAD_REQUEST.value(),"you cannot change ongoing or finished auction to before state")
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
