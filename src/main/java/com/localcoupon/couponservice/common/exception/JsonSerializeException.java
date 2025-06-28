package com.localcoupon.couponservice.common.exception;

public class JsonSerializeException extends BaseException {
    public JsonSerializeException(ErrorCode errorCode) {
        super(errorCode);
    }
}

