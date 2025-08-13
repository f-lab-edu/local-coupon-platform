package com.localcoupon.couponservice.coupon.dto.request;

import jakarta.validation.constraints.NotNull;

public record CouponVerifyRequestDto(@NotNull String qrToken) {}

