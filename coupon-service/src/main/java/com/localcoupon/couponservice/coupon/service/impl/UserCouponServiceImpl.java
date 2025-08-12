package com.localcoupon.couponservice.coupon.service.impl;

import com.localcoupon.common.enums.Result;
import com.localcoupon.couponservice.coupon.dto.response.UserIssuedCouponResponseDto;
import com.localcoupon.couponservice.coupon.entity.Coupon;
import com.localcoupon.couponservice.coupon.enums.UserCouponErrorCode;
import com.localcoupon.couponservice.coupon.exception.UserCouponException;
import com.localcoupon.couponservice.coupon.internal.user.UserServiceClient;
import com.localcoupon.couponservice.coupon.repository.CouponRepository;
import com.localcoupon.couponservice.coupon.repository.IssuedCouponRepository;
import com.localcoupon.couponservice.coupon.service.CouponIssueService;
import com.localcoupon.couponservice.coupon.service.UserCouponService;
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
    private final UserServiceClient userServiceClient;
    private final IssuedCouponRepository issuedCouponRepository;

    @Override
    public List<UserIssuedCouponResponseDto> getUserCoupons() {
        return List.of();
    }


    @Override
    public Result issueCoupon(Long userId, Long couponId, String userEmail) {
        // 1. 쿠폰 조회
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new UserCouponException(UserCouponErrorCode.COUPON_NOT_FOUND));


        //이미 쿠폰 발급이 되어있는지 확인검사한다.
        if (issuedCouponRepository.existsIssuedCouponByCouponIdAndUserId(coupon.getId(), userId)) {
            log.info("[ProcessCouponIssue] 중복 발급 시도: userId={}, couponId={}", userId, coupon.getId());
            return Result.FAIL;
        }

        //3. 비즈니스 처리
        return couponIssueService.processCouponIssue(coupon, userId, userEmail);
    }
}
