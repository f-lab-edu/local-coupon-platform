package com.localcoupon.couponservice.coupon.service;

import com.localcoupon.couponservice.coupon.entity.Coupon;

public interface CouponCacheService {

    void saveCouponForOpen(Coupon coupon);

    boolean isCouponOpen(Long couponId);

    boolean decreaseCouponStock(Long couponId);
}
