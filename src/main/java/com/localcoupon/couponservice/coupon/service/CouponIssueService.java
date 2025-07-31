package com.localcoupon.couponservice.coupon.service;

import com.localcoupon.couponservice.common.enums.Result;
import com.localcoupon.couponservice.coupon.entity.Coupon;
import com.localcoupon.couponservice.user.entity.User;

public interface CouponIssueService {
    Result processCouponIssue(Coupon coupon, User user);
}
