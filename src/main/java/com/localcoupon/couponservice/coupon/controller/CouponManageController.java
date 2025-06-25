package com.localcoupon.couponservice.coupon.controller;

import com.localcoupon.couponservice.coupon.dto.request.CouponCreateRequestDto;
import com.localcoupon.couponservice.coupon.dto.response.CouponResponseDto;
import com.localcoupon.couponservice.coupon.dto.response.CouponVerifyResponseDto;
import com.localcoupon.couponservice.coupon.service.CouponManageService;
import com.localcoupon.couponservice.global.constants.ApiMapping;
import com.localcoupon.couponservice.global.dto.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiMapping.API_Prefix.API_V1)
public class CouponManageController {

    private final CouponManageService couponManageService;

    @PostMapping("/coupons")
    public SuccessResponse<CouponResponseDto> createCoupon(@RequestBody CouponCreateRequestDto request) {
        return SuccessResponse.of(couponManageService.createCoupon(request));
    }

    @PatchMapping("/user-coupons/verify")
    public SuccessResponse<CouponVerifyResponseDto> verifyCoupon(@RequestBody String couponToken) {
        return SuccessResponse.of(couponManageService.verifyCoupon(couponToken));
    }
}
