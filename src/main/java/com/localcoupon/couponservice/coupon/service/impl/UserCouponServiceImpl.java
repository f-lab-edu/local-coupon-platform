package com.localcoupon.couponservice.coupon.service.impl;

import com.localcoupon.couponservice.common.enums.Result;
import com.localcoupon.couponservice.coupon.dto.response.UserIssuedCouponResponseDto;
import com.localcoupon.couponservice.coupon.service.CouponCacheService;
import com.localcoupon.couponservice.coupon.service.UserCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserCouponServiceImpl implements UserCouponService {
    private final CouponCacheService couponCacheService;

    @Override
    public List<UserIssuedCouponResponseDto> getUserCoupons() {
        return List.of();
    }


    @Override
    public Result issueCoupon(Long couponId) {
        if(couponCacheService.isCouponOpen(couponId)) {
            couponCacheService.decreaseCouponStock(couponId);
        }
        return Result.SUCCESS;
    }
}
