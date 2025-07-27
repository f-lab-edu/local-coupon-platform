package com.localcoupon.couponservice.coupon.service.impl;

import com.localcoupon.couponservice.common.enums.Result;
import com.localcoupon.couponservice.common.util.TimeProvider;
import com.localcoupon.couponservice.coupon.dto.CouponPostProcessDto;
import com.localcoupon.couponservice.coupon.dto.response.UserIssuedCouponResponseDto;
import com.localcoupon.couponservice.coupon.entity.Coupon;
import com.localcoupon.couponservice.coupon.entity.IssuedCoupon;
import com.localcoupon.couponservice.coupon.enums.UserCouponErrorCode;
import com.localcoupon.couponservice.coupon.exception.UserCouponException;
import com.localcoupon.couponservice.coupon.repository.CouponRepository;
import com.localcoupon.couponservice.coupon.repository.IssuedCouponRepository;
import com.localcoupon.couponservice.coupon.service.CouponCacheService;
import com.localcoupon.couponservice.coupon.service.CouponPostProcessService;
import com.localcoupon.couponservice.coupon.service.UserCouponService;
import com.localcoupon.couponservice.user.entity.User;
import com.localcoupon.couponservice.user.enums.UserErrorCode;
import com.localcoupon.couponservice.user.exception.UserExcpetion;
import com.localcoupon.couponservice.user.repository.UserRepository;
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
    private final IssuedCouponRepository issuedCouponRepository;
    private final UserRepository userRepository;
    private final TimeProvider timeProvider;

    @Override
    public List<UserIssuedCouponResponseDto> getUserCoupons() {
        return List.of();
    }


    @Override
    public Result issueCoupon(Long userId, Long couponId) {
        // 1. 쿠폰 발급 Validation Early Return
        if (!couponCacheService.isCouponOpen(couponId)) {
            return Result.FAIL; // 쿠폰이 열려있지 않음
        }

        // 2. 쿠폰 재고 처리 and Validation
        if (couponCacheService.decreaseCouponStock(couponId) <= 0) {
            return Result.FAIL; // 재고 감소 실패
        }

        // 3. 쿠폰 조회
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new UserCouponException(UserCouponErrorCode.COUPON_NOT_FOUND));

        // 4. 쿠폰 발급 엔티티 저장을 위한 유저 객체 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserExcpetion(UserErrorCode.USER_NOT_FOUND));

        // 5. 쿠폰 발급 엔티티 저장 (QR 정보는 후처리에서 채움)
        IssuedCoupon issuedCoupon = coupon.issueWithOutQrCode(user, timeProvider.now());
        issuedCouponRepository.save(issuedCoupon);

        // 6. 발급 후 처리 진행
        couponPostProcessService.sendQrCouponToUser(CouponPostProcessDto.of(userId, user.getEmail(), coupon.getTitle(),
                        issuedCoupon.getId(), coupon.getCouponValidStartTime(), coupon.getCouponValidEndTime()), issuedCoupon)
                .orTimeout(5, TimeUnit.SECONDS)
                .exceptionally(ex -> {
                    log.error("[CouponPostProcess 후처리 실패] userId={}, couponId={}", userId, couponId, ex);
                    //TODO 실패 시에는 어떻게 처리할 것인지?
                    return Result.FAIL;
                });;

        return Result.SUCCESS;
    }
}
