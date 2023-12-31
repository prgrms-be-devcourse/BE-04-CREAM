package com.programmers.dev.common;

/**
 * LIVE : 입찰 중
 * OUT_WAREHOUSE : 입고 대기
 * IN_WAREHOUSE : 입고 완료
 * AUTHENTICATED : 검수 합격
 * IN_TRANSACTION : 거래 중
 * AUTHENTICATED_FAILED : 검수 불합격
 * DELIVERING : 배송 중
 * SHIPPED : 배달 완료
 * FINISHED : 거래 완료
 * EXPIRED : 기한 만료
 * CANCELLED : 거래 취소
 */

public enum Status {

    LIVE,

    OUT_WAREHOUSE,

    IN_WAREHOUSE,

    AUTHENTICATED,

    IN_TRANSACTION,

    AUTHENTICATED_FAILED,

    DELIVERING,

    SHIPPED,

    FINISHED,

    EXPIRED,

    RETURN_SHIPPING,

    DELIVERED,

    CANCELLED
}
