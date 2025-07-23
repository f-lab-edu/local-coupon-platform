package com.localcoupon.couponservice.coupon.service;

import com.localcoupon.couponservice.common.enums.Result;
import com.localcoupon.couponservice.coupon.entity.Coupon;

import java.util.concurrent.CompletableFuture;

public interface CouponPostProcessService {
    CompletableFuture<Result> sendQrCouponToUser(Long userId, Coupon Coupon);
}
