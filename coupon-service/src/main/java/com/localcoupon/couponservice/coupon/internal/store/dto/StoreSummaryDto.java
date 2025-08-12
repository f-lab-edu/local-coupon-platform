package com.localcoupon.couponservice.coupon.internal.store.dto;

public record StoreSummaryDto(
        Long id,
        String name,
        String address,
        String category,
        String regionCode,
        Double latitude,
        Double longitude
) {
    public static StoreSummaryDto of(Long id, String name, String address, String category, String regionCode, Double latitude, Double longitude) {
        return new StoreSummaryDto(id, name, address, category, regionCode, latitude, longitude);
    }
}
