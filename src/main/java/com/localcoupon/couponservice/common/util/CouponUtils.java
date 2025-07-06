package com.localcoupon.couponservice.common.util;

public class CouponUtils {
    public static boolean isStockCountPositive(Integer couponStock) {
        return couponStock != null && couponStock > 0;
    }
}
