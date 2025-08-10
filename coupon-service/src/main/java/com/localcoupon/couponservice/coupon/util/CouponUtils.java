package com.localcoupon.couponservice.coupon.util;

public class CouponUtils {
    public static boolean isStockCountPositive(Integer couponStock) {
        return couponStock != null && couponStock > 0;
    }
}
