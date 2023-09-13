package com.programmers.dev.common;

/**
 * LIVE : 입찰 중
 * OUT_WAREHOUSE : 입고 대기
 * IN_WAREHOUSE : 입고 완료
 * AUTHENTICATED : 검수 합격
 * DELIVERING : 배송 중
 * SHIPPED : 배달 완료
 * FINISHED : 거래 완료
 * EXPIRED : 기한 만료
 */

public enum Status {

    LIVE,

    OUT_WAREHOUSE,

    IN_WAREHOUSE,

    AUTHENTICATING,

    AUTHENTICATED,

    DELIVERING,

    SHIPPED,

    FINISHED,

    EXPIRED;
}
