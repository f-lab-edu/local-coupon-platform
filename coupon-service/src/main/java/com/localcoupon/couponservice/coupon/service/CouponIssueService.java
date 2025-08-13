package com.localcoupon.couponservice.coupon.service;

import com.localcoupon.common.enums.Result;
import com.localcoupon.couponservice.coupon.entity.Coupon;

public interface CouponIssueService {

    Coupon saveCouponForOpen(Coupon coupon);

    boolean isCouponOpen(Long couponId);

    int decreaseCouponStock(Long couponId);

    int increaseCouponStock(Long couponId);

    Result processCouponIssue(Coupon coupon, Long userId, String userEmail);
}
