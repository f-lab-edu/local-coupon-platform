package com.localcoupon.couponservice.coupon.service;

import com.localcoupon.couponservice.common.enums.Result;
import com.localcoupon.couponservice.coupon.dto.CouponPostProcessDto;
import com.localcoupon.couponservice.coupon.entity.IssuedCoupon;

import java.util.concurrent.CompletableFuture;

public interface CouponPostProcessService {
    CompletableFuture<Result> sendQrCouponToUser(CouponPostProcessDto couponPostProcessDto, IssuedCoupon issuedCoupon);
}
