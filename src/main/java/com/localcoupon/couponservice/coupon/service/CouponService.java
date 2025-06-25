package com.localcoupon.couponservice.coupon.service;

import com.localcoupon.couponservice.coupon.dto.response.CouponResponseDto;
import com.localcoupon.couponservice.coupon.dto.response.UserIssuedCouponResponseDto;

import java.math.BigDecimal;
import java.util.List;

public interface CouponService {
    List<CouponResponseDto> getAvailableCoupons(BigDecimal lat, BigDecimal lng);
    UserIssuedCouponResponseDto issueCoupon(Long couponId);
}
