package com.localcoupon.couponservice.coupon.service.impl;

import com.localcoupon.couponservice.common.enums.Result;
import com.localcoupon.couponservice.coupon.dto.response.UserIssuedCouponResponseDto;
import com.localcoupon.couponservice.coupon.entity.Coupon;
import com.localcoupon.couponservice.coupon.enums.UserCouponErrorCode;
import com.localcoupon.couponservice.coupon.exception.UserCouponException;
import com.localcoupon.couponservice.coupon.repository.CouponRepository;
import com.localcoupon.couponservice.coupon.service.CouponIssueService;
import com.localcoupon.couponservice.coupon.service.UserCouponService;
import com.localcoupon.couponservice.user.entity.User;
import com.localcoupon.couponservice.user.enums.UserErrorCode;
import com.localcoupon.couponservice.user.exception.UserExcpetion;
import com.localcoupon.couponservice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCouponServiceImpl implements UserCouponService {
    private final CouponIssueService couponIssueService;
    private final CouponRepository couponRepository;
    private final UserRepository userRepository;

    @Override
    public List<UserIssuedCouponResponseDto> getUserCoupons() {
        return List.of();
    }


    @Override
    public Result issueCoupon(Long userId, Long couponId) {
        // 1. 쿠폰 조회
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new UserCouponException(UserCouponErrorCode.COUPON_NOT_FOUND));

        // 2. 쿠폰 발급 엔티티 저장을 위한 유저 객체 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserExcpetion(UserErrorCode.USER_NOT_FOUND));

        //3. 비즈니스 처리
        return couponIssueService.processCouponIssue(coupon, user);
    }
}
