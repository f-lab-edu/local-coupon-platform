package com.localcoupon.couponservice.coupon.controller;

import com.localcoupon.couponservice.coupon.dto.response.UserIssuedCouponResponseDto;
import com.localcoupon.couponservice.coupon.service.UserCouponService;
import com.localcoupon.couponservice.common.constants.ApiMapping;
import com.localcoupon.couponservice.common.dto.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiMapping.USER_COUPON_BASE)
public class UserCouponController {

    private final UserCouponService userCouponService;

    @GetMapping
    public SuccessResponse<List<UserIssuedCouponResponseDto>> getMyCoupons() {
        List<UserIssuedCouponResponseDto> coupons = userCouponService.getUserCoupons();
        return SuccessResponse.of(coupons);
    }

    @PatchMapping("/{id}/use")
    public SuccessResponse<UserIssuedCouponResponseDto> useCoupon(@PathVariable Long id) {
        return SuccessResponse.of(userCouponService.useCoupon(id));
    }
}

