package com.localcoupon.couponservice.coupon.service;

import com.localcoupon.couponservice.common.enums.Result;
import com.localcoupon.couponservice.coupon.entity.Coupon;
import com.localcoupon.couponservice.user.entity.User;

public interface CouponIssueService {

    Coupon saveCouponForOpen(Coupon coupon);

    boolean isCouponOpen(Long couponId);

    int decreaseCouponStock(Long couponId);

    int increaseCouponStock(Long couponId);

    Result processCouponIssue(Coupon coupon, User user);
}
