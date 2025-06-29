package com.localcoupon.couponservice.coupon.service.impl;

import com.localcoupon.couponservice.coupon.dto.response.CouponResponseDto;
import com.localcoupon.couponservice.coupon.dto.response.UserIssuedCouponResponseDto;
import com.localcoupon.couponservice.coupon.service.CouponService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CouponServiceImpl implements CouponService {
    @Override
    public List<CouponResponseDto> getAvailableCoupons(BigDecimal lat, BigDecimal lng) {
        return List.of();
    }

    @Override
    public UserIssuedCouponResponseDto issueCoupon(Long couponId) {
        return null;
    }
}
