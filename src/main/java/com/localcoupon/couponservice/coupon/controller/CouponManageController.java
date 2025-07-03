package com.localcoupon.couponservice.coupon.controller;

import com.localcoupon.couponservice.auth.security.CustomUserDetails;
import com.localcoupon.couponservice.common.constants.ApiMapping;
import com.localcoupon.couponservice.common.dto.response.SuccessResponse;
import com.localcoupon.couponservice.coupon.dto.request.CouponCreateRequestDto;
import com.localcoupon.couponservice.coupon.dto.response.CouponResponseDto;
import com.localcoupon.couponservice.coupon.service.CouponManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiMapping.API_Prefix.API_V1)
public class CouponManageController {

    private final CouponManageService couponManageService;

    @PostMapping("/coupons")
    public SuccessResponse<CouponResponseDto> createCoupon(@RequestBody CouponCreateRequestDto request, @AuthenticationPrincipal CustomUserDetails user) {
        return SuccessResponse.of(couponManageService.createCoupon(request, user.getId()));
    }
}
