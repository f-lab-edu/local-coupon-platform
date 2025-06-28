package com.localcoupon.couponservice.coupon.controller;

import com.localcoupon.couponservice.coupon.dto.response.CouponResponseDto;
import com.localcoupon.couponservice.coupon.dto.response.UserIssuedCouponResponseDto;
import com.localcoupon.couponservice.coupon.service.CouponService;
import com.localcoupon.couponservice.common.constants.ApiMapping;
import com.localcoupon.couponservice.common.dto.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

import static com.localcoupon.couponservice.common.constants.ApiMapping.API_Prefix;

@RestController
@RequiredArgsConstructor
@RequestMapping(API_Prefix.API_V1 + ApiMapping.COUPON)
public class CouponController {

    private final CouponService couponService;

    @GetMapping
    public SuccessResponse<List<CouponResponseDto>> getAvailableCoupons(
            @RequestParam("lat") BigDecimal lat,
            @RequestParam("lng") BigDecimal lng
    ) {
        List<CouponResponseDto> coupons = couponService.getAvailableCoupons(lat,lng);
        return SuccessResponse.of(coupons);
    }


    @PostMapping("/{couponId}/issue")
    public SuccessResponse<UserIssuedCouponResponseDto> issueCoupon(@PathVariable Long couponId) {
        UserIssuedCouponResponseDto issuedCoupon = couponService.issueCoupon(couponId);
        return SuccessResponse.of(issuedCoupon);
    }
}
