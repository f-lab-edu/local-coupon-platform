package com.localcoupon.couponservice.coupon.service;

import com.localcoupon.couponservice.coupon.dto.CouponPostProcessDto;
import com.localcoupon.couponservice.coupon.entity.IssuedCoupon;

public interface CouponPostProcessService {
    void sendQrCouponToUser(CouponPostProcessDto couponPostProcessDto, IssuedCoupon issuedCoupon);
}
