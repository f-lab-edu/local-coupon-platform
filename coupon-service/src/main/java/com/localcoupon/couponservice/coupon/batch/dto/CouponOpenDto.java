package com.localcoupon.couponservice.coupon.batch.dto;

import java.time.LocalDateTime;

public record CouponOpenDto(Long id, Long couponLimit, LocalDateTime couponIssueEndTime) {
    public static CouponOpenDto of(Long id, Long couponLimit, LocalDateTime couponIssueEndTime) {
        return new CouponOpenDto(id, couponLimit, couponIssueEndTime);
    }
}
