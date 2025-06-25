package com.localcoupon.couponservice.coupon.dto.response;

import java.time.LocalDateTime;

public record UserIssuedCouponResponseDto(
        Long id,
        String couponTitle,
        String description,
        LocalDateTime issuedAt,
        LocalDateTime couponValidStartTime,
        LocalDateTime couponValidEndTime,
        boolean isUsed,
        String displayCode,
        String qrToken
) {}

