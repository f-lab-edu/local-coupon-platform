package com.localcoupon.otherservice.common.util;

public class CouponUtils {
    public static boolean isStockCountPositive(Integer couponStock) {
        return couponStock != null && couponStock > 0;
    }
}
