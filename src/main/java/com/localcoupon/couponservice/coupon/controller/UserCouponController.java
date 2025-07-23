package com.localcoupon.couponservice.coupon.controller;

import com.localcoupon.couponservice.auth.security.CustomUserDetails;
import com.localcoupon.couponservice.common.annotation.PreventDuplicateRequest;
import com.localcoupon.couponservice.common.constants.ApiMapping;
import com.localcoupon.couponservice.common.dto.response.SuccessResponse;
import com.localcoupon.couponservice.common.enums.Result;
import com.localcoupon.couponservice.coupon.dto.response.UserIssuedCouponResponseDto;
import com.localcoupon.couponservice.coupon.service.UserCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiMapping.USER_COUPON_BASE)
public class UserCouponController {
    private final UserCouponService userCouponService;

    @PreventDuplicateRequest
    @PostMapping("/{couponId}/issue")
    public SuccessResponse<Result> issueCoupon(@AuthenticationPrincipal CustomUserDetails loginUser,
                                               @PathVariable("couponId") Long couponId) {
        Result result = userCouponService.issueCoupon(loginUser.getId(), couponId);
        return SuccessResponse.of(result);
    }

    @GetMapping
    public SuccessResponse<List<UserIssuedCouponResponseDto>> getMyCoupons() {
        List<UserIssuedCouponResponseDto> coupons = userCouponService.getUserCoupons();
        return SuccessResponse.of(coupons);
    }
}

