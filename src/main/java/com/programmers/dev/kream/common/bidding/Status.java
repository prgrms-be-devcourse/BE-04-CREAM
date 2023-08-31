package com.programmers.dev.kream.common.bidding;

/**
 * LIVE : 입찰 중
 * IN_WAREHOUSE : 입고 완료
 * AUTHENTICATED : 검수 합격
 * DELIVERING : 배송 중
 * SHIPPED : 배달 완료
 * FINISHED : 거래 완료
 * EXPIRED : 기한 만료
 */

public enum Status {

    LIVE,

    IN_WAREHOUSE,

    AUTHENTICATED,

    DELIVERING,

    SHIPPED,

    FINISHED,

    EXPIRED;
}
