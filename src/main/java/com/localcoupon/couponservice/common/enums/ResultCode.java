package com.localcoupon.couponservice.common.enums;

public enum ResultCode {
    SUCCESS(1),
    FAIL(0);
    private int code;

    ResultCode(int value) {
        this.code = value;
    }

    public int getValue() {
        return code;
    }
}
