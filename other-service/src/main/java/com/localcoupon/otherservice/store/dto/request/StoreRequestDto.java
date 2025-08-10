package com.localcoupon.otherservice.store.dto.request;

import com.localcoupon.otherservice.store.enums.StoreCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StoreRequestDto(
        @NotBlank String name,
        @NotBlank String address,
        @NotNull StoreCategory category,
        String phoneNumber,
        String description,
        String imageUrl
) {}
