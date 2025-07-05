package com.localcoupon.couponservice.coupon.dto.request;

import com.localcoupon.couponservice.coupon.enums.CouponScope;

import java.time.LocalDateTime;

public record CouponCreateRequestDto(String title, String description, CouponScope scope,
                                     int totalCount, LocalDateTime couponValidStartTime, LocalDateTime couponValidEndTime,
                                     LocalDateTime couponIssueStartTime, LocalDateTime couponIssueEndTime) {
}

