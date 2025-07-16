package com.localcoupon.couponservice.coupon.service;

import com.localcoupon.couponservice.common.dto.request.CursorPageRequest;
import com.localcoupon.couponservice.common.dto.response.ResultResponseDto;
import com.localcoupon.couponservice.coupon.dto.request.CouponCreateRequestDto;
import com.localcoupon.couponservice.coupon.dto.request.CouponUpdateRequestDto;
import com.localcoupon.couponservice.coupon.dto.response.CouponResponseDto;
import com.localcoupon.couponservice.coupon.dto.response.CouponVerifyResponseDto;

import java.util.List;

public interface CouponManageService {

    CouponResponseDto createCoupon(CouponCreateRequestDto request, Long ownerId);

    List<CouponResponseDto> getCouponsByOwner(Long ownerId, CursorPageRequest request);

    CouponResponseDto getCouponDetail(Long couponId);

    CouponResponseDto updateCoupon(Long couponId, Long userId, CouponUpdateRequestDto request);

    ResultResponseDto deleteCoupon(Long couponId, Long userId);

    CouponVerifyResponseDto verifyCoupon(String qrToken);
}
