package com.localcoupon.couponservice.coupon.service;

import com.localcoupon.couponservice.coupon.entity.IssuedCoupon;

public interface CouponPostProcessService {
    void sendQrCouponToUser(String userEmail, IssuedCoupon issuedCoupon);
}
