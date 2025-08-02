package com.localcoupon.couponservice.coupon.dto.request;

import com.localcoupon.couponservice.common.util.validation.ValidCouponCount;
import com.localcoupon.couponservice.coupon.entity.CouponPeriod;
import com.localcoupon.couponservice.coupon.enums.CouponScope;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CouponCreateRequestDto(@NotNull String title, @NotBlank String description, @NotNull CouponScope scope,
                                     @ValidCouponCount int totalCount, @Valid CouponPeriod validPeriod,
                                     @Valid CouponPeriod issuePeriod) {
}

