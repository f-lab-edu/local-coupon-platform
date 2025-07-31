package com.localcoupon.couponservice.coupon.service;

import com.localcoupon.couponservice.coupon.entity.Coupon;

public interface CouponCacheService {

    Coupon saveCouponForOpen(Coupon coupon);

    boolean isCouponOpen(Long couponId);

    int decreaseCouponStock(Long couponId);

    int increaseCouponStock(Long couponId);
}
