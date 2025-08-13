package com.localcoupon.couponservice.coupon.common.exception;

public class CommonException extends BaseException {
    public CommonException(ErrorCode errorCode) {
        super(errorCode);
    }
}
