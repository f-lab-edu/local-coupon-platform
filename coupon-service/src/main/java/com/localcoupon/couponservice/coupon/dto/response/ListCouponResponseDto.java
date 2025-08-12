package com.localcoupon.couponservice.coupon.dto.response;

import com.localcoupon.couponservice.coupon.entity.Coupon;
import com.localcoupon.couponservice.coupon.internal.store.dto.StoreSummaryDto;

import java.util.List;

public record ListCouponResponseDto(
        List<CouponResponseDto> couponResponseDtos,
        StoreSummaryDto storeResponse
) {
    public static ListCouponResponseDto from(Coupon coupon, StoreSummaryDto storeSummaryDto) {
        return new ListCouponResponseDto(
                List.of(CouponResponseDto.from(coupon)),
                storeSummaryDto
        );
    }

    public static ListCouponResponseDto from(List<Coupon> coupon,  StoreSummaryDto storeSummaryDto) {
        if (coupon.isEmpty()) {
            // 비어있을 때 처리
            return new ListCouponResponseDto(List.of(), null);
        }

        return new ListCouponResponseDto(
                coupon.stream()
                        .map(CouponResponseDto::from)
                        .toList(),
                storeSummaryDto
        );
    }
}


