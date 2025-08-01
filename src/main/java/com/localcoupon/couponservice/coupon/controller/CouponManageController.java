package com.localcoupon.couponservice.coupon.controller;

import com.localcoupon.couponservice.auth.security.CustomUserDetails;
import com.localcoupon.couponservice.common.annotation.CursorRequest;
import com.localcoupon.couponservice.common.constants.ApiMapping;
import com.localcoupon.couponservice.common.dto.request.CursorPageRequest;
import com.localcoupon.couponservice.common.dto.response.SuccessResponse;
import com.localcoupon.couponservice.common.enums.Result;
import com.localcoupon.couponservice.coupon.dto.request.CouponCreateRequestDto;
import com.localcoupon.couponservice.coupon.dto.request.CouponUpdateRequestDto;
import com.localcoupon.couponservice.coupon.dto.request.CouponVerifyRequestDto;
import com.localcoupon.couponservice.coupon.dto.response.CouponVerifyResponseDto;
import com.localcoupon.couponservice.coupon.dto.response.CouponResponseDto;
import com.localcoupon.couponservice.coupon.dto.response.ListCouponResponseDto;
import com.localcoupon.couponservice.coupon.service.CouponManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiMapping.COUPON_MANAGE_BASE)
public class CouponManageController {

    private final CouponManageService couponManageService;

    @GetMapping("/coupons")
    public SuccessResponse<ListCouponResponseDto> getCoupons(
            @AuthenticationPrincipal CustomUserDetails user,
            @CursorRequest CursorPageRequest request
    ) {
        return SuccessResponse.of(
                couponManageService.getCouponsByOwner(user.getId(), request)
        );
    }

    @PostMapping("/coupons")
    public SuccessResponse<CouponResponseDto> createCoupon(@RequestBody CouponCreateRequestDto request, @AuthenticationPrincipal CustomUserDetails user) {
        return SuccessResponse.of(couponManageService.createCoupon(request, user.getId()));
    }

    @GetMapping("/coupons/{couponId}")
    public SuccessResponse<CouponResponseDto> getCouponDetail(
            @PathVariable Long couponId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return SuccessResponse.of(
                couponManageService.getCouponDetail(couponId)
        );
    }

    @PatchMapping("/coupons/{couponId}")
    public SuccessResponse<CouponResponseDto> updateCoupon(
            @PathVariable Long couponId,
            @RequestBody CouponUpdateRequestDto request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return SuccessResponse.of(
                couponManageService.updateCoupon(couponId, user.getId(), request)
        );
    }

    @DeleteMapping("/coupons/{couponId}")
    public SuccessResponse<Result> deleteCoupon(
            @PathVariable Long couponId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return SuccessResponse.of(
                couponManageService.deleteCoupon(couponId, user.getId())
        );
    }

    @PostMapping("/coupons/verify")
    public SuccessResponse<CouponVerifyResponseDto> verifyCoupon(@RequestBody CouponVerifyRequestDto request,
                                                                 @AuthenticationPrincipal CustomUserDetails user) {
        CouponVerifyResponseDto response = couponManageService.verifyCoupon(request.qrToken(), user.getId());
        return SuccessResponse.of(response);
    }
}
