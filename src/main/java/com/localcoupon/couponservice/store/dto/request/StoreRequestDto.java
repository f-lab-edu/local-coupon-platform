package com.localcoupon.couponservice.store.dto.request;

import com.localcoupon.couponservice.store.enums.StoreCategory;

public record StoreRequestDto(
        String name,
        String address,
        StoreCategory category
) {}
