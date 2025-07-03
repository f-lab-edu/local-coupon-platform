package com.localcoupon.couponservice.coupon.controller;

import com.localcoupon.couponservice.common.constants.ApiMapping;
import com.localcoupon.couponservice.coupon.service.UserCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiMapping.COUPON)
public class CouponController {

    private final UserCouponService couponService;


}
