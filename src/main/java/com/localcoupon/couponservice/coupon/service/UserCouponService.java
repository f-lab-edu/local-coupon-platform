package com.localcoupon.couponservice.coupon.service;

import com.localcoupon.couponservice.common.enums.Result;
import com.localcoupon.couponservice.coupon.dto.response.UserIssuedCouponResponseDto;

import java.util.List;

public interface UserCouponService {

    List<UserIssuedCouponResponseDto> getUserCoupons();

    Result issueCoupon(Long userId, Long couponId);
}
