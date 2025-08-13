package com.localcoupon.couponservice.coupon.common.contract.store;


import java.math.BigDecimal;
import java.time.LocalDateTime;

public record StoreResponseDto(
        Long id,
        String name,
        String address,
        StoreCategory category,
        BigDecimal latitude,
        BigDecimal longitude,
        String phoneNumber,
        String description,
        String imageUrl,
        LocalDateTime createdAt
) {
}


