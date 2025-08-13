package com.localcoupon.couponservice.coupon.common.util;

public class CouponUtils {
    public static boolean isStockCountPositive(Integer couponStock) {
        return couponStock != null && couponStock > 0;
    }
}
