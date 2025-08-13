package com.localcoupon.couponservice.coupon.internal.store.dto;

import com.localcoupon.couponservice.coupon.common.contract.store.StoreCategory;
import com.localcoupon.couponservice.coupon.common.contract.store.StoreResponseDto;

import java.math.BigDecimal;

public record StoreSummaryDto(
        Long id,
        String name,
        String address,
        StoreCategory category,
        BigDecimal latitude,
        BigDecimal longitude
) {
    public static StoreSummaryDto of(StoreResponseDto storeResponseDto) {
        return new StoreSummaryDto(storeResponseDto.id(), storeResponseDto.name(),
                storeResponseDto.address(), storeResponseDto.category(), storeResponseDto.latitude(),
                storeResponseDto.longitude());
    }
}
