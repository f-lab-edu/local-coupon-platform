package com.localcoupon.couponservice.coupon.dto.request;

import com.localcoupon.couponservice.coupon.entity.CouponPeriod;
import com.localcoupon.couponservice.coupon.enums.CouponScope;

public record CouponCreateRequestDto(String title, String description, CouponScope scope,
                                     int totalCount, CouponPeriod validPeriod,
                                     CouponPeriod issuePeriod) {
}

