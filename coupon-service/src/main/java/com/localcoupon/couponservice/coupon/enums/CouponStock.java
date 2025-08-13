package com.localcoupon.couponservice.coupon.enums;
public enum CouponStock {
    SUCCESS(1),
    SOLD_OUT(0),
    INIT(0);

    private final int value;

    CouponStock(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

