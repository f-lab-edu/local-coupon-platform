package com.localcoupon.couponservice.coupon.service;

import com.localcoupon.couponservice.coupon.entity.IssuedCoupon;
import com.localcoupon.couponservice.user.entity.User;

public interface CouponPostProcessService {
    void sendQrCouponToUser(User user, IssuedCoupon issuedCoupon);
}
