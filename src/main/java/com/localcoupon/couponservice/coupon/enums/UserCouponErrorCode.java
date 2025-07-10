package com.localcoupon.couponservice.coupon.enums;

import com.localcoupon.couponservice.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum UserCouponErrorCode implements ErrorCode {
    SOLD_OUT_COUPON("쿠폰이 모두 소진되었습니다.", HttpStatus.NO_CONTENT),
    ALREADY_COUPON_USED("이미 사용된 쿠폰 입니다.", HttpStatus.BAD_REQUEST),
    INVALID_COUPON("쿠폰 유효기간이 만료되었습니다.", HttpStatus.BAD_REQUEST),
    ENDED_COUPON_ISSUE("쿠폰 발급기간이 종료되었습니다.", HttpStatus.BAD_REQUEST),
    COUPON_LOCK_FAILED("쿠폰 락 처리에 실패하였습니다.", HttpStatus.CONFLICT),
    COUPON_KEY_PARSING_FAILED("쿠폰 키 파싱 처리에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String message;
    private final HttpStatus status;

    UserCouponErrorCode(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    @Override
    public String getCode() {
        return this.name();
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }
}
