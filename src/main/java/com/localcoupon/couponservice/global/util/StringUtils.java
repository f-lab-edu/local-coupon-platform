package com.localcoupon.couponservice.global.util;

public class StringUtils {
    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
