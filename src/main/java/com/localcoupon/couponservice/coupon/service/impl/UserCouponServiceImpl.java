package com.localcoupon.couponservice.coupon.service.impl;

import com.localcoupon.couponservice.coupon.dto.response.CouponResponseDto;
import com.localcoupon.couponservice.coupon.dto.response.UserIssuedCouponResponseDto;
import com.localcoupon.couponservice.coupon.service.UserCouponService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class UserCouponServiceImpl implements UserCouponService {
    @Override
    public List<UserIssuedCouponResponseDto> getUserCoupons() {
        return List.of();
    }

    @Override
    public UserIssuedCouponResponseDto useCoupon(Long couponId) {
        return null;
    }

    @Override
    public List<CouponResponseDto> getAvailableCoupons(BigDecimal lat, BigDecimal lng) {
        return List.of();
    }

    @Override
    public UserIssuedCouponResponseDto issueCoupon(Long couponId) {
        return null;
    }
}
