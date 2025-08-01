package com.localcoupon.couponservice.coupon.service.impl;

import com.localcoupon.couponservice.common.enums.Result;
import com.localcoupon.couponservice.common.infra.RedisProperties;
import com.localcoupon.couponservice.common.util.TimeProvider;
import com.localcoupon.couponservice.coupon.entity.Coupon;
import com.localcoupon.couponservice.coupon.entity.IssuedCoupon;
import com.localcoupon.couponservice.coupon.enums.UserCouponErrorCode;
import com.localcoupon.couponservice.coupon.exception.UserCouponException;
import com.localcoupon.couponservice.coupon.repository.CouponRedisRepository;
import com.localcoupon.couponservice.coupon.repository.IssuedCouponRepository;
import com.localcoupon.couponservice.coupon.service.CouponIssueService;
import com.localcoupon.couponservice.coupon.service.CouponPostProcessService;
import com.localcoupon.couponservice.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@Primary
@Slf4j
public class CouponIssueServiceImpl implements CouponIssueService {

    private final CouponRedisRepository couponRedisRepository;
    private final RedisProperties redisProperties;
    private final IssuedCouponRepository issuedCouponRepository;
    private final CouponPostProcessService couponPostProcessService;
    private final TimeProvider timeProvider;

    @Override
    public Coupon saveCouponForOpen(Coupon coupon) {
        String key = redisProperties.couponOpenPrefix() + coupon.getId();

        long ttlSeconds = Duration.between(
                LocalDateTime.now(),
                coupon.getValidPeriod().getEnd().plusDays(1)
        ).getSeconds();

        couponRedisRepository.saveData(
                key,
                coupon.getTotalCount(),
                Duration.ofSeconds(ttlSeconds)
        );
        return coupon;
    }

    @Override
    public boolean isCouponOpen(Long couponId) {
        String key = redisProperties.couponOpenPrefix() + couponId;
        return couponRedisRepository.exists(key);
    }

    @Override
    public int decreaseCouponStock(Long couponId) {
        String lockKey = redisProperties.couponLockPrefix() + couponId;
        String dataKey = redisProperties.couponOpenPrefix() + couponId;

        if (!isCouponOpen(couponId)) {
            throw new UserCouponException(UserCouponErrorCode.ENDED_COUPON_ISSUE);
        }

        return couponRedisRepository.executeWithLock(
                lockKey,
                2000,
                5000,
                () -> couponRedisRepository.decreaseCouponStock(dataKey)
                        .orElseThrow(() -> new UserCouponException(UserCouponErrorCode.SOLD_OUT_COUPON))
        );
    }

    @Override
    public int increaseCouponStock(Long couponId) {
        String lockKey = redisProperties.couponLockPrefix() + couponId;
        String dataKey = redisProperties.couponOpenPrefix() + couponId;

        return couponRedisRepository.executeWithLock(
                lockKey,
                2000,
                5000,
                () -> couponRedisRepository.increaseCouponStock(dataKey)
                        .orElseThrow(() -> new UserCouponException(UserCouponErrorCode.SOLD_OUT_COUPON))
        );
    }

    @Override
    @Transactional
    public Result processCouponIssue(Coupon coupon, User user) {
        try {
            // 1. 쿠폰 재고 처리
            decreaseCouponStock(coupon.getId());
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
            increaseCouponStock(couponId); // 재고 복구
        } catch (Exception ex) {
            //TODO 재고 복구가 실패하면 로그를 남기고 추가적인 처리 필요
            log.error("[handleFailedIssueCoupon] 쿠폰 재고 복구 실패", ex);
        }
    }
}
