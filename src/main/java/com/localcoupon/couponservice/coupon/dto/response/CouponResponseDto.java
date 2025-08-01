package com.localcoupon.couponservice.coupon.dto.response;

import com.localcoupon.couponservice.coupon.entity.Coupon;
import com.localcoupon.couponservice.coupon.entity.CouponPeriod;
import com.localcoupon.couponservice.coupon.enums.CouponScope;

public record CouponResponseDto (Long id,
                                 String title,
                                 String description,
                                 CouponScope scope,
                                 int totalCount,
                                 int issuedCount,
                                 CouponPeriod validPeriod,
                                 CouponPeriod issuePeriod
) {
    public static CouponResponseDto from(Coupon coupon) {
        return new CouponResponseDto(
                coupon.getId(),
                coupon.getTitle(),
                coupon.getDescription(),
                coupon.getScope(),
                coupon.getTotalCount(),
                coupon.getIssuedCount(),
                coupon.getValidPeriod(),
                coupon.getIssuePeriod()
        );
    }
}
