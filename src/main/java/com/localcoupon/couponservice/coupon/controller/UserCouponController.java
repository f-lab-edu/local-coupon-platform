package com.localcoupon.couponservice.coupon.controller;

import com.localcoupon.couponservice.common.annotation.PreventDuplicateRequest;
import com.localcoupon.couponservice.common.constants.ApiMapping;
import com.localcoupon.couponservice.common.dto.response.SuccessResponse;
import com.localcoupon.couponservice.common.enums.ResultCode;
import com.localcoupon.couponservice.coupon.dto.response.UserIssuedCouponResponseDto;
import com.localcoupon.couponservice.coupon.service.UserCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiMapping.USER_COUPON_BASE)
public class UserCouponController {
    private final UserCouponService userCouponService;

    @PreventDuplicateRequest
    @PostMapping("/{couponId}/issue")
    public SuccessResponse<ResultCode> issueCoupon(@PathVariable("couponId") Long couponId) {
        ResultCode resultCode = userCouponService.issueCoupon(couponId);
        return SuccessResponse.of(resultCode);
    }

    @GetMapping
    public SuccessResponse<List<UserIssuedCouponResponseDto>> getMyCoupons() {
        List<UserIssuedCouponResponseDto> coupons = userCouponService.getUserCoupons();
        return SuccessResponse.of(coupons);
    }
}

