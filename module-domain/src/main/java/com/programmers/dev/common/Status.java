package com.programmers.dev.common;

/**
 * LIVE : 입찰 중
 * IN_WAREHOUSE : 입고 완료
 * AUTHENTICATED : 검수 합격
 * IN_TRANSACTION : 입찰 체결
 * DELIVERING : 배송 중
 * SHIPPED : 배달 완료
 * FINISHED : 거래 완료
 * EXPIRED : 기한 만료
 */

public enum Status {

    LIVE,

    IN_WAREHOUSE,

    AUTHENTICATED,

    IN_TRANSACTION,

    DELIVERING,

    SHIPPED,

    FINISHED,

    EXPIRED;
}
