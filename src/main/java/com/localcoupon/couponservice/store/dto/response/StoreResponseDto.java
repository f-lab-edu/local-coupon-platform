package com.localcoupon.couponservice.store.dto.response;

import java.time.LocalDateTime;

public record StoreResponseDto(
        Long id,
        String name,
        String address,
        String category,
        String regionCode,
        double latitude,
        double longitude,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}


