package com.localcoupon.couponservice.coupon.service;

import com.localcoupon.couponservice.coupon.dto.response.UserIssuedCouponResponseDto;

import java.util.List;

public interface UserCouponService {

    List<UserIssuedCouponResponseDto> getUserCoupons();

    UserIssuedCouponResponseDto useCoupon(Long couponId);

    UserIssuedCouponResponseDto issueCoupon(Long couponId);
}
