package com.localcoupon.couponservice.coupon.dto;

import java.time.LocalDateTime;

public record CouponPostProcessDto (Long userId, String userEmail, String couponTitle, Long issuedCouponId, LocalDateTime issuedCouponValidStartTime, LocalDateTime issuedCouponValidEndTime) {
    public static CouponPostProcessDto of(Long userId, String userEmail, String couponTitle, Long issuedCouponId, LocalDateTime issuedCouponValidStartTime, LocalDateTime issuedCouponValidEndTime) {
        return new CouponPostProcessDto(userId, userEmail, couponTitle, issuedCouponId, issuedCouponValidStartTime, issuedCouponValidEndTime);
    }
}
