package com.localcoupon.couponservice.common.enums;

public enum Result {
    SUCCESS(1),
    FAIL(0);
    private final int code;

    Result(int value) {
        this.code = value;
    }

    public int getValue() {
        return code;
    }

    public boolean isSuccess() {
        return this == Result.SUCCESS;
    }
}
