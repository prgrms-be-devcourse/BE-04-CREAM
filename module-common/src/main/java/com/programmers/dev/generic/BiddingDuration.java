package com.programmers.dev.generic;

public enum BiddingDuration {

    DAY(1L),

    WEEK(7L),

    MONTH(30L);

    private final Long days;

    BiddingDuration(Long days) {
        this.days = days;
    }

    public Long getDays() {
        return days;
    }
}
