package com.localcoupon.couponservice.coupon.controller;

import com.localcoupon.common.enums.Result;
import com.localcoupon.couponservice.coupon.annotation.CursorRequest;
import com.localcoupon.couponservice.coupon.common.dto.response.SuccessResponse;
import com.localcoupon.couponservice.coupon.dto.CursorPageRequest;
import com.localcoupon.couponservice.coupon.dto.request.CouponCreateRequestDto;
import com.localcoupon.couponservice.coupon.dto.request.CouponUpdateRequestDto;
import com.localcoupon.couponservice.coupon.dto.request.CouponVerifyRequestDto;
import com.localcoupon.couponservice.coupon.dto.response.CouponResponseDto;
import com.localcoupon.couponservice.coupon.dto.response.CouponVerifyResponseDto;
import com.localcoupon.couponservice.coupon.dto.response.ListCouponResponseDto;
import com.localcoupon.couponservice.coupon.service.CouponManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.localcoupon.common.constants.ApiMapping.COUPON_MANAGE_BASE;

@RestController
@RequiredArgsConstructor
@RequestMapping(COUPON_MANAGE_BASE)
public class CouponManageController {

    private final CouponManageService couponManageService;

    @GetMapping("/coupons")
    public SuccessResponse<ListCouponResponseDto> getCoupons(
            @RequestHeader("X-USER-ID") Long userId,
            @CursorRequest CursorPageRequest request
    ) {
        return SuccessResponse.of(
                couponManageService.getCouponsByOwner(userId, request)
        );
    }

    @PostMapping("/coupons")
    public SuccessResponse<CouponResponseDto> createCoupon(@RequestBody CouponCreateRequestDto request,
                                                           @RequestHeader("X-USER-ID") Long userId) {
        return SuccessResponse.of(couponManageService.createCoupon(request, userId));
    }

    @GetMapping("/coupons/{couponId}")
    public SuccessResponse<CouponResponseDto> getCouponDetail(
            @PathVariable Long couponId,
            @RequestHeader("X-USER-ID") Long userId
    ) {
        return SuccessResponse.of(
                couponManageService.getCouponDetail(couponId)
        );
    }

    @PatchMapping("/coupons/{couponId}")
    public SuccessResponse<CouponResponseDto> updateCoupon(
            @PathVariable Long couponId,
            @RequestBody CouponUpdateRequestDto request,
            @RequestHeader("X-USER-ID") Long userId
    ) {
        return SuccessResponse.of(
                couponManageService.updateCoupon(couponId, userId, request)
        );
    }

    @DeleteMapping("/coupons/{couponId}")
    public SuccessResponse<Result> deleteCoupon(
            @PathVariable Long couponId,
            @RequestHeader("X-USER-ID") Long userId
    ) {
        return SuccessResponse.of(
                couponManageService.deleteCoupon(couponId, userId)
        );
    }

    @PostMapping("/coupons/verify")
    public SuccessResponse<CouponVerifyResponseDto> verifyCoupon(@RequestBody CouponVerifyRequestDto request,
                                                                 @RequestHeader("X-USER-ID") Long userId) {
        CouponVerifyResponseDto response = couponManageService.verifyCoupon(request.qrToken(), userId);
        return SuccessResponse.of(response);
    }
}
