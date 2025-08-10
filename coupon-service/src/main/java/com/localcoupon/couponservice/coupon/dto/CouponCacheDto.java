package com.localcoupon.couponservice.coupon.dto;

import com.localcoupon.couponservice.coupon.entity.Coupon;

import java.util.HashMap;
import java.util.Map;

public record CouponCacheDto(
        Long id,
        String title,
        String description,
        String scope,
        Integer totalCount,
        Integer issuedCount,
        String validStartTime,
        String validEndTime
) {
    public static CouponCacheDto from(Coupon coupon) {
        return new CouponCacheDto(
                coupon.getId(),
                coupon.getTitle(),
                coupon.getDescription(),
                coupon.getScope().name(),
                coupon.getTotalCount(),
                coupon.getIssuedCount(),
                coupon.getValidPeriod().getStart().toString(),
                coupon.getValidPeriod().getEnd().toString()
        );
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("title", title);
        map.put("description", description);
        map.put("scope", scope);
        map.put("totalCount", totalCount);
        map.put("issuedCount", issuedCount);
        map.put("validStartTime", validStartTime);
        map.put("validEndTime", validEndTime);
        return map;
    }
}

