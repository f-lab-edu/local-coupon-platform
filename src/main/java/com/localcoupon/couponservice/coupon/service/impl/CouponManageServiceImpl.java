package com.localcoupon.couponservice.coupon.service.impl;

import com.localcoupon.couponservice.coupon.dto.request.CouponCreateRequestDto;
import com.localcoupon.couponservice.coupon.dto.response.CouponResponseDto;
import com.localcoupon.couponservice.coupon.dto.response.CouponVerifyResponseDto;
import com.localcoupon.couponservice.coupon.service.CouponManageService;
import org.springframework.stereotype.Service;

@Service
public class CouponManageServiceImpl implements CouponManageService {
    @Override
    public CouponResponseDto createCoupon(CouponCreateRequestDto request) {
        return null;
    }

    @Override
    public CouponVerifyResponseDto verifyCoupon(String couponToken) {
        return null;
    }
}
