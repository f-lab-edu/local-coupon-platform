package com.localcoupon.couponservice.store.dto.response;

import com.localcoupon.couponservice.store.entity.Store;
import com.localcoupon.couponservice.store.enums.StoreCategory;
import com.localcoupon.couponservice.store.enums.StoreErrorCode;
import com.localcoupon.couponservice.store.exception.StoreException;

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
    public static StoreResponseDto of(Long id, String name, String address, StoreCategory category,
                                      String phoneNumber, String description, BigDecimal latitude,
                                      BigDecimal longitude, String imageUrl, LocalDateTime createdAt) {
        return new StoreResponseDto(id, name, address, category, latitude, longitude,
                phoneNumber, description, imageUrl, createdAt);
    }
    public static StoreResponseDto fromEntity(Store store) {
        if(store == null) {
            throw new StoreException(StoreErrorCode.STORE_NOT_FOUND_EXCEPTION);
        }
        return new StoreResponseDto(store.getId(), store.getName(), store.getAddress(), store.getCategory(),
                store.getLatitude(), store.getLongitude(), store.getPhoneNumber(), store.getDescription(),
                store.getImageUrl(), store.getCreatedAt());
    }
}


