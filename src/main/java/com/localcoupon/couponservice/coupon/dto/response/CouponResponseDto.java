package com.localcoupon.couponservice.coupon.dto.response;

import com.localcoupon.couponservice.coupon.entity.Coupon;
import com.localcoupon.couponservice.coupon.enums.CouponScope;
import com.localcoupon.couponservice.store.dto.response.StoreResponseDto;

import java.time.LocalDateTime;

public record CouponResponseDto(
        Long id,
        String title,
        String description,
        CouponScope scope,
        int totalCount,
        int issuedCount,
        LocalDateTime couponValidStartTime,
        LocalDateTime couponValidEndTime,
        LocalDateTime couponIssueStartTime,
        LocalDateTime couponIssueEndTime,
        StoreResponseDto storeResponse
) {
    public static CouponResponseDto from(Coupon coupon) {
        return new CouponResponseDto(
                coupon.getId(),
                coupon.getTitle(),
                coupon.getDescription(),
                coupon.getScope(),
                coupon.getTotalCount(),
                coupon.getIssuedCount(),
                coupon.getCouponValidStartTime(),
                coupon.getCouponValidEndTime(),
                coupon.getCouponIssueStartTime(),
                coupon.getCouponIssueEndTime(),
                coupon.getStore() != null ? StoreResponseDto.fromEntity(coupon.getStore()) : null
        );
    }
}


