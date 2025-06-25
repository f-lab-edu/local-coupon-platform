package com.localcoupon.couponservice.coupon.service;

import com.localcoupon.couponservice.coupon.dto.request.CouponCreateRequestDto;
import com.localcoupon.couponservice.coupon.dto.response.CouponResponseDto;
import com.localcoupon.couponservice.coupon.dto.response.CouponVerifyResponseDto;

public interface CouponManageService {
    CouponResponseDto createCoupon(CouponCreateRequestDto request);
    CouponVerifyResponseDto verifyCoupon(String couponToken);
}

