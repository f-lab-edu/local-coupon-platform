package com.localcoupon.couponservice.coupon.dto.response;

import com.localcoupon.couponservice.coupon.enums.CouponScope;

import java.time.LocalDateTime;

public record CouponResponseDto(
        Long id,
        String title,
        String description,
        CouponScope scope,
        int totalCount,
        int issuedCount,
        LocalDateTime couponValidStartTime,
        LocalDateTime couponValidEndTime,
        String storeName
) {}


