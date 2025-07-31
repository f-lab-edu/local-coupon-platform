package com.localcoupon.couponservice.coupon.service.impl;

import com.localcoupon.couponservice.common.enums.Result;
import com.localcoupon.couponservice.common.util.TimeProvider;
import com.localcoupon.couponservice.coupon.entity.Coupon;
import com.localcoupon.couponservice.coupon.entity.IssuedCoupon;
import com.localcoupon.couponservice.coupon.repository.IssuedCouponRepository;
import com.localcoupon.couponservice.coupon.service.CouponCacheService;
import com.localcoupon.couponservice.coupon.service.CouponIssueService;
import com.localcoupon.couponservice.coupon.service.CouponPostProcessService;
import com.localcoupon.couponservice.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponIssueServieImpl implements CouponIssueService {
    private final CouponCacheService couponCacheService;
    private final IssuedCouponRepository issuedCouponRepository;
    private final TimeProvider timeProvider;
    private final CouponPostProcessService couponPostProcessService;

    @Override
    @Transactional
    public Result processCouponIssue(Coupon coupon, User user) {
        try {
            // 1. 쿠폰 재고 처리
            couponCacheService.decreaseCouponStock(coupon.getId());
            // 2. 쿠폰 발급 진행 (QR 정보는 후처리에서 업데이트)
            IssuedCoupon issuedCoupon = IssuedCoupon.of(user, coupon, timeProvider.now());
            // 3. 쿠폰 발급 저장
            issuedCouponRepository.save(issuedCoupon);
            // 4. 쿠폰 후처리 비동기 로직 실행 (qr 발송)
            couponPostProcessService.sendQrCouponToUser(user, issuedCoupon);
            return Result.SUCCESS;
        } catch (Exception e) {
            // 예외가 발생한 경우 롤백 작업 수행
            log.error("[ProcessCouponIssue]에서 예외가 발생했습니다.", e);
            handleFailedIssueCoupon(coupon.getId()); // 보상 트랜잭션(재고 복구)
            return Result.FAIL;
        }
    }
    // 보상 트랜잭션: 쿠폰 발급 실패 시 재고를 복구하는 메서드
    private void handleFailedIssueCoupon(Long couponId) {
        try {
            couponCacheService.increaseCouponStock(couponId); // 재고 복구
        } catch (Exception ex) {
            //TODO 재고 복구가 실패하면 로그를 남기고 추가적인 처리 필요
            log.error("[handleFailedIssueCoupon] 쿠폰 재고 복구 실패", ex);
        }
    }
}
