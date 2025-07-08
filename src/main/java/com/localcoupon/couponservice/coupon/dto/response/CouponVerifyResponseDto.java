package com.localcoupon.couponservice.coupon.dto.response;


public record CouponVerifyResponseDto(
        Long couponId,
        boolean verified
) {
    public static CouponVerifyResponseDto of(Long couponId, boolean verified) {
        return new CouponVerifyResponseDto(couponId, verified);
    }
}


