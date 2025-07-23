package com.localcoupon.couponservice.coupon.service.impl;

import com.localcoupon.couponservice.common.enums.Result;
import com.localcoupon.couponservice.coupon.dto.response.UserIssuedCouponResponseDto;
import com.localcoupon.couponservice.coupon.entity.Coupon;
import com.localcoupon.couponservice.coupon.enums.UserCouponErrorCode;
import com.localcoupon.couponservice.coupon.exception.UserCouponException;
import com.localcoupon.couponservice.coupon.repository.CouponRepository;
import com.localcoupon.couponservice.coupon.service.CouponCacheService;
import com.localcoupon.couponservice.coupon.service.CouponPostProcessService;
import com.localcoupon.couponservice.coupon.service.UserCouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCouponServiceImpl implements UserCouponService {
    private final CouponCacheService couponCacheService;
    private final CouponPostProcessService couponPostProcessService;
    private final CouponRepository couponRepository;

    @Override
    public List<UserIssuedCouponResponseDto> getUserCoupons() {
        return List.of();
    }


    @Override
    public Result issueCoupon(Long userId, Long couponId) {
        //쿠폰 발급 Validation Early Return
        if (!couponCacheService.isCouponOpen(couponId)) {
            return Result.FAIL; // 쿠폰이 열려있지 않음
        }

        if (couponCacheService.decreaseCouponStock(couponId) <= 0) {
            return Result.FAIL; // 재고 감소 실패
        }

        //성공 처리
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new UserCouponException(UserCouponErrorCode.COUPON_NOT_FOUND));

        couponPostProcessService.sendQrCouponToUser(userId, coupon)
                .orTimeout(5, TimeUnit.SECONDS)
                .exceptionally(ex -> {
                    log.error("[CouponPostProcess 후처리 실패] userId={}, couponId={}", userId, couponId, ex);
                    //TODO 실패 시에는 어떻게 처리할 것인지?
                    return Result.FAIL;
                });;

        return Result.SUCCESS;
    }
}
