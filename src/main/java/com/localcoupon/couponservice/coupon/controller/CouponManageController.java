package com.localcoupon.couponservice.coupon.controller;

import com.localcoupon.couponservice.auth.security.CustomUserDetails;
import com.localcoupon.couponservice.common.constants.ApiMapping;
import com.localcoupon.couponservice.common.dto.request.CursorPageRequest;
import com.localcoupon.couponservice.common.dto.response.ResultResponseDto;
import com.localcoupon.couponservice.common.dto.response.SuccessResponse;
import com.localcoupon.couponservice.coupon.dto.request.CouponCreateRequestDto;
import com.localcoupon.couponservice.coupon.dto.request.CouponUpdateRequestDto;
import com.localcoupon.couponservice.coupon.dto.response.CouponResponseDto;
import com.localcoupon.couponservice.coupon.service.CouponManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiMapping.COUPON_MANAGE_BASE)
public class CouponManageController {

    private final CouponManageService couponManageService;

    @PostMapping("/coupons")
    public SuccessResponse<CouponResponseDto> createCoupon(@RequestBody CouponCreateRequestDto request, @AuthenticationPrincipal CustomUserDetails user) {
        return SuccessResponse.of(couponManageService.createCoupon(request, user.getId()));
    }

    @GetMapping("/coupons")
    public SuccessResponse<List<CouponResponseDto>> getCoupons(
            @AuthenticationPrincipal CustomUserDetails user,
            @ModelAttribute CursorPageRequest request
    ) {
        return SuccessResponse.of(
                couponManageService.getCouponsByOwner(user.getId(), request)
        );
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
    public SuccessResponse<ResultResponseDto> deleteCoupon(
            @PathVariable Long couponId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return SuccessResponse.of(
                couponManageService.deleteCoupon(couponId, user.getId())
        );
    }
}
