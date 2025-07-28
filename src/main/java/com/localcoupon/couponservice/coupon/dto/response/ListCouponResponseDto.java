package com.localcoupon.couponservice.coupon.dto.response;

import com.localcoupon.couponservice.coupon.entity.Coupon;
import com.localcoupon.couponservice.store.dto.response.StoreResponseDto;

import java.util.List;

public record ListCouponResponseDto(
        List<CouponResponseDto> couponResponseDtos,
        StoreResponseDto storeResponse
) {
    public static ListCouponResponseDto from(Coupon coupon) {
        return new ListCouponResponseDto(
                List.of(CouponResponseDto.from(coupon)),
                StoreResponseDto.fromEntity(coupon.getStore())
        );
    }

    public static ListCouponResponseDto from(List<Coupon> coupon) {
        if (coupon == null || coupon.isEmpty()) {
            // 비어있을 때 처리
            return new ListCouponResponseDto(List.of(), null);
        }

        return new ListCouponResponseDto(
                coupon.stream()
                        .map(CouponResponseDto::from)
                        .toList(),
                StoreResponseDto.fromEntity(coupon.get(0).getStore())
        );
    }
}


