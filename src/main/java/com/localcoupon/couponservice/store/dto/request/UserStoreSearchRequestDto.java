package com.localcoupon.couponservice.store.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UserStoreSearchRequestDto(
        @NotNull
        @DecimalMin(value = "32.0", message = "위도는 32.0 이상이어야 합니다.")
        @DecimalMax(value = "39.5", message = "위도는 39.5 이하여야 합니다.")
        BigDecimal minLatitude,
        @DecimalMin(value = "32.0", message = "위도는 32.0 이상이어야 합니다.")
        @DecimalMax(value = "39.5", message = "위도는 39.5 이하여야 합니다.")
        BigDecimal maxLatitude,
        @NotNull
        @DecimalMin(value = "123.0", inclusive = true, message = "경도는 123.0 이상이어야 합니다.")
        @DecimalMax(value = "134.0", inclusive = true, message = "경도는 134.0 이하여야 합니다.")
        BigDecimal minLongitude,
        @NotNull
        @DecimalMin(value = "123.0", inclusive = true, message = "경도는 123.0 이상이어야 합니다.")
        @DecimalMax(value = "134.0", inclusive = true, message = "경도는 134.0 이하여야 합니다.")
        BigDecimal maxLongitude
) {}
