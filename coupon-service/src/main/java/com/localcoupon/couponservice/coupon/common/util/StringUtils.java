package com.localcoupon.couponservice.coupon.common.util;

public class StringUtils {
    public static boolean hasText(String value) {
        return !isEmpty(value);
    }
    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
